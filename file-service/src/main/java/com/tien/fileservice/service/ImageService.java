package com.tien.fileservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cloudinary.utils.ObjectUtils;
import com.mongodb.lang.Nullable;
import com.tien.sharedcommon.converter.MediaConverter;
import com.tien.sharedcontacts.media.ImageUploadEvent;
import com.tien.sharedcontacts.media.ImageUploadedEvent;
import com.tien.sharedcontacts.media.MultipleImageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.tien.fileservice.dto.response.UploadResponse;
import com.tien.fileservice.entity.Image;
import com.tien.sharedcontacts.media.entity.ImageType;
import com.tien.fileservice.entity.ImageVersions;
import com.tien.fileservice.exception.AppException;
import com.tien.fileservice.exception.ErrorCode;
import com.tien.fileservice.mapper.ImageMapper;
import com.tien.fileservice.repository.ImageRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif", "image/avif"
    );

    Cloudinary cloudinary;

    ImageRepository imageRepository;

    ImageMapper imageMapper;

    KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public MultipleImageResponse uploadImages(ImageUploadEvent event) {
        // validate giống single
        if (event.files() == null || event.files().isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
        if (StringUtils.isBlank(event.ownerId())) {
            throw new AppException(ErrorCode.OWNER_ID_REQUIRED);
        }
        if (event.imageType() == ImageType.POST_IMAGE && event.postId() == null) {
            throw new AppException(ErrorCode.POST_ID_REQUIRED);
        }

        List<ImageUploadedEvent> uploadedEvents = new ArrayList<>();

        try {
            for (int i = 0; i < event.files().size(); i++) {
                String base64 = event.files().get(i);

                Map<String, Object> props = null;
                if (event.propertiesMap() != null && i < event.propertiesMap().length) {
                    props = event.propertiesMap()[i];
                }

                ImageUploadedEvent uploadedEvent = uploadImage(
                        base64,
                        event.imageType(),
                        event.ownerId(),
                        event.postId(),
                        props
                );
                uploadedEvents.add(uploadedEvent);
            }
        } catch (Exception e) {
            log.error("Upload multiple images failed", e);
        }

        return new MultipleImageResponse(uploadedEvents);
    }

    @Transactional
    public ImageUploadedEvent uploadImage(ImageUploadEvent event) throws IOException {
        if (event.files() == null || event.files().isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
        if (StringUtils.isBlank(event.ownerId())) {
            throw new AppException(ErrorCode.OWNER_ID_REQUIRED);
        }
        if (event.imageType() == ImageType.POST_IMAGE && event.postId() == null) {
            throw new AppException(ErrorCode.POST_ID_REQUIRED);
        }

        String base64 = event.files().get(0);

        Map<String, Object> props = null;
        if (event.propertiesMap() != null && event.propertiesMap().length > 0) {
            props = event.propertiesMap()[0];
        }

        return uploadImage(base64, event.imageType(), event.ownerId(), event.postId(), props);
    }


    // upload single image json
    private ImageUploadedEvent uploadImage(String base64, ImageType imageType, String ownerId, String postId, Map<String, Object> properties)
            throws IOException {
        if (base64 == null || base64.isBlank()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
        // Decode Base64 to bytes
        byte[] bytes = MediaConverter.decodeFromBase64(base64);

        final String folder = buildFolder(imageType, ownerId, postId);

        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "image",
                "unique_filename", true,   // tránh đụng public_id
                "overwrite", false,        // không ghi đè ngẫu nhiên
                "invalidate", true,        // xóa cache CDN khi cần (có ích nếu sau này overwrite)
                "quality", "auto",
                "fetch_format", "auto",
                "use_filename", false
        );

        final Map<?, ?> uploadResult;
        try {
            uploadResult = cloudinary.uploader().upload(bytes, options);
        } catch (Exception ex) {
            throw new AppException(ErrorCode.CLOUDINARY_UPLOAD_FAILED);
        }

        String publicId = (String) uploadResult.get("public_id");
        String url = (String) uploadResult.get("secure_url");
        String format = uploadResult.get("format") != null ? uploadResult.get("format").toString() : null;
        Integer width = (Integer) uploadResult.get("width");
        Integer height = (Integer) uploadResult.get("height");
        String version = uploadResult.get("version") != null ? uploadResult.get("version").toString() : null;

        // Determine content type from format
        String contentType = format != null ? "image/" + format.toLowerCase() : "image/jpeg";
        Long size = (long) bytes.length;

        // Save to MongoDB
        Image image = Image.builder()
                .ownerId(ownerId)
                .postId(postId)
                .contentType(contentType)
                .size(size)
                .imageType(imageType)
                .secureUrl(url)
                .publicId(publicId)
                .format(format)
                .width(width)
                .height(height)
                .imageVersions(generateImageVersions(publicId))
                .version(version)
                .build();
        image = imageRepository.save(image);
        log.info("Saved image to MongoDB with id: {}", image.getId());

        Map<String, Object> safeProps = (properties == null)
                ? Map.of()
                : Map.copyOf(properties);

        // uploaded callback
        return new ImageUploadedEvent(publicId, url, safeProps);
    }

    //upload multiple image form data
    @Transactional
    public List<UploadResponse> uploadMultipleImages(List<MultipartFile> files,
                                                     ImageType imageType,
                                                     String ownerId,
                                                     @Nullable String postId) throws IOException {
        List<UploadResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            responses.add(uploadImage(file, imageType, ownerId, postId));
        }
        return responses;
    }

    //upload single image form data
    @Transactional
    public UploadResponse uploadImage(MultipartFile file,
                                      ImageType imageType,
                                      String ownerId,
                                      @Nullable String postId)
            throws IOException {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
        if (StringUtils.isBlank(ownerId)) {
            throw new AppException(ErrorCode.OWNER_ID_REQUIRED);
        }
        if (imageType == ImageType.POST_IMAGE && postId == null) {
            throw new AppException(ErrorCode.POST_ID_REQUIRED);
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new AppException(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }

        final String folder = buildFolder(imageType, ownerId, postId);

        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "image",
                "unique_filename", true,       // tránh đụng public_id
                "overwrite", false,            // không ghi đè ngẫu nhiên
                "invalidate", true,            // xóa cache CDN khi cần
                "quality", "auto",
                "fetch_format", "auto",
                "use_filename", false
        );

        final Map<?, ?> uploadResult;
        try {
            uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        } catch (Exception ex) {
            throw new AppException(ErrorCode.CLOUDINARY_UPLOAD_FAILED);
        }

        Image image = Image.builder()
                .ownerId(ownerId)
                .postId(postId)
                .contentType(file.getContentType())
                .size(file.getSize())
                .imageType(imageType)
                .secureUrl(uploadResult.get("secure_url").toString())
                .publicId(uploadResult.get("public_id").toString())
                .format(uploadResult.get("format").toString())
                .width((Integer) uploadResult.get("width"))
                .height((Integer) uploadResult.get("height"))
                .imageVersions(generateImageVersions(uploadResult.get("public_id").toString()))
                .version(uploadResult.get("version").toString())
                .build();
        image = imageRepository.save(image);

        return imageMapper.toUploadResponse(image);
    }

    private String buildUrl(String publicId, int width, int height, String crop) {
        return cloudinary
                .url()
                .transformation(new Transformation().width(width).height(height).crop(crop))
                .secure(true)
                .generate(publicId);
    }

    private ImageVersions generateImageVersions(String publicId) {
        return new ImageVersions(
                buildUrl(publicId, 150, 150, "fill"),
                buildUrl(publicId, 500, 500, "limit"),
                buildUrl(publicId, 1200, 1200, "limit"),
                cloudinary.url().secure(true).generate(publicId));
    }

    private String buildFolder(ImageType imageType, String ownerId, String postId) {
        return switch (imageType) {
            case AVATAR -> "avatars/%s".formatted(ownerId);
            case POST_IMAGE -> "posts/%s/%s".formatted(ownerId, postId);
            case BACKGROUND_IMAGE ->  "backgrounds/%s".formatted(ownerId);
        };
    }

}
