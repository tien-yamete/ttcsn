package com.tien.postservice.service;

import com.tien.postservice.dto.PageResponse;
import com.tien.postservice.dto.request.PostRequest;
import com.tien.postservice.dto.response.PostResponse;
import com.tien.postservice.dto.response.UserProfileResponse;
import com.tien.postservice.entity.Post;
import com.tien.postservice.exception.AppException;
import com.tien.postservice.exception.ErrorCode;
import com.tien.postservice.mapper.PostMapper;
import com.tien.postservice.repository.PostRepository;
import com.tien.postservice.repository.httpclient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    DateTimeFormatter dateTimeFormatter;
    PostRepository postRepository;
    PostMapper postMapper;
    ProfileClient profileClient;
    ImageUploadKafkaService imageUploadKafkaService;

    public PostResponse createPost(String content, List<MultipartFile> images) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId =  authentication.getName();

        boolean hasContent = content != null && !content.trim().isEmpty();
        boolean hasImages = images != null && !images.isEmpty();

        if (!hasContent && !hasImages) {
            throw new AppException(ErrorCode.POST_EMPTY);
        }

        Post post = Post.builder()
                .content(content)
                .userId(userId)
                .createdDate(Instant.now())
                .modifiedDate(Instant.now())
                .build();

        post = postRepository.save(post);

        List<String> imageUrls = List.of();
        if (images != null && !images.isEmpty()) {
            try{
                imageUrls = imageUploadKafkaService.uploadPostImages(images, userId, post.getId());

                post.setImageUrls(imageUrls);
                post.setModifiedDate(Instant.now());
                post = postRepository.save(post);
            }
            catch (Exception e) {
                log.error("Failed to upload images for post: {}", post.getId(), e);
            }
        }
        return postMapper.toPostResponse(post);
    }

    public PageResponse<PostResponse> getMyPosts(int page, int size){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId =  authentication.getName();

        UserProfileResponse userProfile = null;

        try {
            userProfile = profileClient.getProfile(userId).getResult();
        } catch (Exception e) {
            log.error("Error while getting user profile", e);
        }

        Pageable pageable = PageRequest.of(page - 1, size,  Sort.by("createdDate").descending());

        var pageData = postRepository.findAllByUserId(userId, pageable);

        String username = userProfile != null ? userProfile.getUsername() : null;
        var postList = pageData.getContent().stream().map(post -> {
            var postResponse = postMapper.toPostResponse(post);
            postResponse.setCreated(dateTimeFormatter.format(post.getCreatedDate()));
            postResponse.setUsername(username);
            return postResponse;
        }).toList();

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }
}
