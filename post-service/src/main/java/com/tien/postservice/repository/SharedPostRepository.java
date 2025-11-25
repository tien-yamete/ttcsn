package com.tien.postservice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tien.postservice.entity.SharedPost;

@Repository
public interface SharedPostRepository extends MongoRepository<SharedPost, String> {
    Page<SharedPost> findAllByUserId(String userId, Pageable pageable);

    Page<SharedPost> findAllByPostId(String postId, Pageable pageable);

    long countByPostId(String postId);

    void deleteAllByPostId(String postId);

    Optional<SharedPost> findByUserIdAndPostId(String userId, String postId);
}
