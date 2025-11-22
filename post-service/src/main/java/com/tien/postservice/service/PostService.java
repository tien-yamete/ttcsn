package com.tien.postservice.service;

import com.tien.postservice.dto.PageResponse;
import com.tien.postservice.dto.response.PostResponse;
import com.tien.postservice.dto.response.UserProfileResponse;
import com.tien.postservice.entity.Post;
import com.tien.postservice.entity.PrivacyType;
import com.tien.postservice.entity.SavedPost;
import com.tien.postservice.entity.SharedPost;
import com.tien.postservice.exception.AppException;
import com.tien.postservice.exception.ErrorCode;
import com.tien.postservice.mapper.PostMapper;
import com.tien.postservice.repository.PostRepository;
import com.tien.postservice.repository.SavedPostRepository;
import com.tien.postservice.repository.SharedPostRepository;
import com.tien.postservice.repository.httpclient.ProfileClient;
import com.tien.postservice.repository.httpclient.SocialClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    DateTimeFormatter dateTimeFormatter;
    PostRepository postRepository;
    PostMapper postMapper;
    ProfileClient profileClient;
    SocialClient socialClient;
    ImageUploadKafkaService imageUploadKafkaService;

    SavedPostRepository savedPostRepository;

    SharedPostRepository sharedPostRepository;

    public PostResponse createPost(String content, List<MultipartFile> images, PrivacyType privacy) {
        String userId = getCurrentUserId();

        boolean hasContent = content != null && !content.trim().isEmpty();
        boolean hasImages = images != null && !images.isEmpty();

        if (!hasContent && !hasImages) {
            throw new AppException(ErrorCode.POST_EMPTY);
        }

        // Default privacy is PUBLIC if not specified
        if (privacy == null) {
            privacy = PrivacyType.PUBLIC;
        }

        Post post = Post.builder()
                .content(content)
                .userId(userId)
                .privacy(privacy)
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
        return buildPostResponse(post, userId);
    }

    public PageResponse<PostResponse> getMyPosts(int page, int size){
        String userId = getCurrentUserId();

        UserProfileResponse userProfile = getUserProfile(userId);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        var pageData = postRepository.findAllByUserId(userId, pageable);

        var postList = pageData.getContent().stream()
                .map(post -> {
                    var postResponse = postMapper.toPostResponse(post);
                    enrichPostResponse(postResponse, post, userProfile);
                    return postResponse;
                })
                .toList();

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }

    public void savePost(String postId){
        String userId = getCurrentUserId();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if(savedPostRepository.existsByUserIdAndPostId(userId, postId)){
            throw new AppException(ErrorCode.POST_ALREADY_SAVED);
        }

        SavedPost savedPost = SavedPost.builder()
                .userId(userId)
                .postId(postId)
                .build();

        savedPostRepository.save(savedPost);
        log.info("Saved post: {}", savedPost);
    }

    public void unsavePost(String postId){
        String userId = getCurrentUserId();

        SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_SAVED));

        savedPostRepository.delete(savedPost);
        log.info("Unsaved post: {}", savedPost);
    }

    public PageResponse<PostResponse> getSavedPosts(int page, int size) {
        String userId = getCurrentUserId();

        UserProfileResponse userProfileResponse = getUserProfile(userId);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("savedDate").descending());
        var pageData = savedPostRepository.findAllByUserId(userId, pageable);

        var postList = pageData.getContent().stream()
                .map(savedPost -> {
                    Post post = postRepository.findById(savedPost.getPostId()).orElse(null);
                    if (post == null) {
                        return null;
                    }
                    var postResponse = postMapper.toPostResponse(post);
                    enrichPostResponse(postResponse, post, userProfileResponse);
                    return postResponse;
                })
                .filter(post -> post != null)
                .toList();

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }

    public PostResponse sharePost(String postId, String content) {
        String userId = getCurrentUserId();

        // Kiểm tra post có tồn tại không
        Post originalPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // Kiểm tra quyền share: chỉ có thể share PUBLIC posts hoặc posts của chính mình
        if (originalPost.getPrivacy() == PrivacyType.PRIVATE && !originalPost.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        SharedPost sharedPost = SharedPost.builder()
                .userId(userId)
                .postId(postId)
                .content(content)
                .sharedDate(Instant.now())
                .build();

        sharedPost = sharedPostRepository.save(sharedPost);

        // Tạo post mới từ bài viết được chia sẻ
        Post newPost = Post.builder()
                .userId(userId)
                .content(content != null && !content.trim().isEmpty() ? content : null)
                .imageUrls(originalPost.getImageUrls())
                .privacy(PrivacyType.PUBLIC) // Shared posts are always public
                .createdDate(Instant.now())
                .modifiedDate(Instant.now())
                .build();

        newPost = postRepository.save(newPost);
        return buildPostResponse(newPost, userId);
    }

    public PageResponse<PostResponse> getSharedPosts(String postId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("sharedDate").descending());
        var pageData = sharedPostRepository.findAllByPostId(postId, pageable);

        var postList = pageData.getContent().stream()
                .map(sharedPost -> {
                    Post post = postRepository.findById(sharedPost.getPostId()).orElse(null);
                    if (post == null) {
                        return null;
                    }
                    return buildPostResponse(post, sharedPost.getUserId());
                })
                .filter(post -> post != null)
                .toList();

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }

    public long getShareCount(String postId) {
        return sharedPostRepository.countByPostId(postId);
    }

    public boolean isPostSaved(String postId) {
        String userId = getCurrentUserId();
        return savedPostRepository.existsByUserIdAndPostId(userId, postId);
    }

    public PostResponse getPostById(String postId) {
        String userId = getCurrentUserId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        
        // Kiểm tra quyền xem: nếu post là PRIVATE, chỉ owner mới xem được
        if (post.getPrivacy() == PrivacyType.PRIVATE && !post.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        
        return buildPostResponse(post, post.getUserId());
    }

    public PostResponse updatePost(String postId, String content, List<MultipartFile> images, PrivacyType privacy) {
        String userId = getCurrentUserId();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // Kiểm tra quyền sở hữu
        if (!post.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.POST_NOT_OWNER);
        }

        boolean hasContent = content != null && !content.trim().isEmpty();
        boolean hasImages = images != null && !images.isEmpty();

        if (!hasContent && !hasImages && (post.getImageUrls() == null || post.getImageUrls().isEmpty())) {
            throw new AppException(ErrorCode.POST_EMPTY);
        }

        // Cập nhật nội dung
        if (content != null) {
            post.setContent(content);
        }

        // Cập nhật privacy nếu có
        if (privacy != null) {
            post.setPrivacy(privacy);
        }

        // Cập nhật ảnh nếu có
        if (hasImages) {
            try {
                List<String> imageUrls = imageUploadKafkaService.uploadPostImages(images, userId, post.getId());
                post.setImageUrls(imageUrls);
            } catch (Exception e) {
                log.error("Failed to upload images for post: {}", post.getId(), e);
            }
        }

        post.setModifiedDate(Instant.now());
        post = postRepository.save(post);
        return buildPostResponse(post, userId);
    }

    public void deletePost(String postId) {
        String userId = getCurrentUserId();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // Kiểm tra quyền sở hữu
        if (!post.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.POST_NOT_OWNER);
        }

        // Xóa các saved post liên quan
        savedPostRepository.deleteAllByPostId(postId);

        // Xóa các shared post liên quan
        sharedPostRepository.deleteAllByPostId(postId);

        // Xóa post
        postRepository.delete(post);
    }

    public PageResponse<PostResponse> getPostsByUserId(String userId, int page, int size) {
        String currentUserId = getCurrentUserId();
        UserProfileResponse userProfile = getUserProfile(userId);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        var pageData = postRepository.findAllByUserId(userId, pageable);
        
        // Nếu không phải owner, chỉ hiển thị PUBLIC posts
        boolean isOwner = userId.equals(currentUserId);
        var postList = pageData.getContent().stream()
                .filter(post -> isOwner || post.getPrivacy() == PrivacyType.PUBLIC)
                .map(post -> {
                    var postResponse = postMapper.toPostResponse(post);
                    enrichPostResponse(postResponse, post, userProfile);
                    return postResponse;
                })
                .toList();

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }

    public PageResponse<PostResponse> getMySharedPosts(int page, int size) {
        String userId = getCurrentUserId();
        UserProfileResponse userProfile = getUserProfile(userId);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("sharedDate").descending());
        var pageData = sharedPostRepository.findAllByUserId(userId, pageable);

        var postList = pageData.getContent().stream()
                .map(sharedPost -> {
                    Post post = postRepository.findById(sharedPost.getPostId()).orElse(null);
                    if (post == null) {
                        return null;
                    }
                    var postResponse = postMapper.toPostResponse(post);
                    enrichPostResponse(postResponse, post, userProfile);
                    return postResponse;
                })
                .filter(post -> post != null)
                .toList();

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }

    public long getSavedCount() {
        String userId = getCurrentUserId();
        return savedPostRepository.findAllByUserId(userId, Pageable.unpaged()).getTotalElements();
    }

    public PageResponse<PostResponse> searchPosts(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        String userId = getCurrentUserId();
        
        // Lấy danh sách blocked users
        Set<String> blockedUserIds = getBlockedUserIds(userId);
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        
        // Search trong PUBLIC posts và posts của chính user, loại trừ blocked users
        List<String> excludedUserIds = List.copyOf(blockedUserIds);
        var pageData = postRepository.searchPublicPosts(
                userId, 
                keyword.trim(), 
                excludedUserIds, 
                pageable);

        var postList = pageData.getContent().stream()
                .map(post -> buildPostResponse(post, post.getUserId()))
                .toList();

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }

    public PageResponse<PostResponse> getPublicPosts(int page, int size) {
        String userId = getCurrentUserId();
        
        // Lấy danh sách blocked users và tạo mutable Set
        Set<String> blockedUserIds = new HashSet<>(getBlockedUserIds(userId));
//        blockedUserIds.add(userId); // Loại trừ cả posts của chính user hiện tại

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        
        // Chỉ lấy PUBLIC posts và loại trừ blocked users (bao gồm chính user)
        List<String> excludedUserIds = List.copyOf(blockedUserIds);
        var pageData = postRepository.findByPrivacyAndUserIdNotIn(
                PrivacyType.PUBLIC, 
                excludedUserIds, 
                pageable);

        var postList = pageData.getContent().stream()
                .map(post -> buildPostResponse(post, post.getUserId()))
                .toList();

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }

    private Set<String> getBlockedUserIds(String userId) {
        try {
            var response = socialClient.getBlockedUserIds();
            if (response != null && response.getResult() != null) {
                return new HashSet<>(response.getResult());
            }
        } catch (Exception e) {
            log.error("Error while getting blocked user IDs for userId: {}", userId, e);
        }
        return Set.of();
    }

    // Helper methods
    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private UserProfileResponse getUserProfile(String userId) {
        try {
            return profileClient.getProfile(userId).getResult();
        } catch (Exception e) {
            log.error("Error while getting user profile for userId: {}", userId, e);
            return null;
        }
    }

    private void enrichPostResponse(PostResponse postResponse, Post post, UserProfileResponse userProfile) {
        postResponse.setCreated(dateTimeFormatter.format(post.getCreatedDate()));
        if (userProfile != null) {
            // Hiển thị họ + tên thay vì username
            String displayName = getDisplayName(userProfile.getFirstName(), userProfile.getLastName(), userProfile.getUsername());
            postResponse.setUsername(displayName);
            postResponse.setUserAvatar(userProfile.getAvatar());
        }
        
        // Set interaction stats
        String currentUserId = getCurrentUserId();
        postResponse.setIsSaved(savedPostRepository.existsByUserIdAndPostId(currentUserId, post.getId()));
        postResponse.setShareCount(sharedPostRepository.countByPostId(post.getId()));
        
        // TODO: likeCount, commentCount, isLiked cần gọi interaction-service
        // Tạm thời set null hoặc 0
        postResponse.setLikeCount(0);
        postResponse.setCommentCount(0);
        postResponse.setIsLiked(false);
    }

    private String getDisplayName(String firstName, String lastName, String username) {
        if (firstName != null && !firstName.trim().isEmpty() && lastName != null && !lastName.trim().isEmpty()) {
            return (firstName.trim() + " " + lastName.trim()).trim();
        } else if (firstName != null && !firstName.trim().isEmpty()) {
            return firstName.trim();
        } else if (lastName != null && !lastName.trim().isEmpty()) {
            return lastName.trim();
        } else {
            // Fallback to username if no first/last name
            return username != null ? username : "";
        }
    }

    private PostResponse buildPostResponse(Post post, String userId) {
        var postResponse = postMapper.toPostResponse(post);
        UserProfileResponse userProfile = getUserProfile(userId);
        enrichPostResponse(postResponse, post, userProfile);
        return postResponse;
    }
}
