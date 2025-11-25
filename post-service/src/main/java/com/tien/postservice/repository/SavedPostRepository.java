package com.tien.postservice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tien.postservice.entity.SavedPost;

@Repository
public interface SavedPostRepository extends MongoRepository<SavedPost, String> {
    Optional<SavedPost> findByUserIdAndPostId(String userId, String postId);

    Page<SavedPost> findAllByUserId(String userId, Pageable pageable);

    boolean existsByUserIdAndPostId(String userId, String postId);

    void deleteAllByPostId(String postId);
}
