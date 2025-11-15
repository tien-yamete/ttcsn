package com.tien.fileservice.controller;

import java.io.IOException;
import java.util.List;

import com.tien.sharedcontacts.media.ImageUploadEvent;
import com.tien.sharedcontacts.media.ImageUploadedEvent;
import com.tien.sharedcontacts.media.MultipleImageResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tien.fileservice.dto.response.UploadResponse;
import com.tien.sharedcontacts.media.entity.ImageType;
import com.tien.fileservice.service.ImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class CloudMediaController {

    private final ImageService imageService;

//    @PostMapping(value = "/upload-multiple")
//    public ResponseEntity<MultipleImageResponse> uploadMultipleImages(@RequestBody ImageUploadEvent event) {
//        var response = imageService.uploadImages(event);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping(value = "/upload")
    public ResponseEntity<ImageUploadedEvent> uploadImage(@RequestBody ImageUploadEvent imageUploadEvent) throws IOException {
        var response = imageService.uploadImage(imageUploadEvent);
        return ResponseEntity.ok(response);
    }


    @PostMapping(value = "/upload-form-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> uploadImageFD(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") ImageType imageType,
            @RequestParam("ownerId") String ownerId,
            @RequestParam(value = "postId", required = false) String postId)
            throws IOException {
        UploadResponse response = imageService.uploadImage(file, imageType, ownerId, postId);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/upload-multiple-form-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<UploadResponse>> uploadMultipleImagesFD(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("type") ImageType imageType,
            @RequestParam("ownerId") String ownerId,
            @RequestParam(value = "postId", required = false) String postId) throws IOException {
        List<UploadResponse> responses = imageService.uploadMultipleImages(files, imageType, ownerId, postId);
        return ResponseEntity.ok(responses);
    }

    //    @GetMapping("/images/{publicId}")
    //    public ResponseEntity<ImageResponse> getImageInfo(@PathVariable String publicId) {
    //        try {
    //            ImageResponse imageResponse = mediaService.getImageInfo(publicId);
    //            return ResponseEntity.ok(imageResponse);
    //        } catch (Exception e) {
    //            return ResponseEntity.notFound().build();
    //        }
    //    }
    //
    //    @GetMapping("/download/{publicId}")
    //    public ResponseEntity<byte[]> downloadImage(@PathVariable String publicId) {
    //        try {
    //            String decodedPublicId = publicId.replace("_", "/");
    //            byte[] imageData = mediaService.downloadImage(decodedPublicId);
    //
    //            HttpHeaders headers = new HttpHeaders();
    //            headers.setContentType(MediaType.IMAGE_PNG);
    //
    //            headers.setContentDispositionFormData("attachment", publicId + ".jpg");
    //
    //            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    //        } catch (IOException e) {
    //            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    //        }
    //    }
    //
    //    @DeleteMapping("delete/{publicId}")
    //    public ResponseEntity<DeleteImageResponse> deleteImage(@PathVariable String publicId) {
    //        //System.out.println("delete" + publicId);
    //        String decodedPublicId = publicId.replace("_", "/");
    //        DeleteImageResponse response = null;
    //        try {
    //            response = mediaService.deleteImage(decodedPublicId);
    //        } catch (IOException e) {
    //            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    //        }
    //        if (response.isSuccess()) {
    //            return ResponseEntity.ok(response);
    //        }
    //        return ResponseEntity.badRequest().body(response);
    //    }
    //
    //    @DeleteMapping("/batch")
    //    public ResponseEntity<Void> deleteMultipleImages(@RequestBody List<String> publicIds) throws Exception {
    //        mediaService.deleteMultipleImages(publicIds);
    //        return ResponseEntity.noContent().build();
    //    }
}
