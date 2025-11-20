package com.tien.postservice.repository;

import com.tien.postservice.entity.Post;
import com.tien.postservice.entity.PrivacyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    Page<Post> findAllByUserId(String userId, Pageable pageable);
    Page<Post> findByPrivacyAndUserIdNotIn(PrivacyType privacy, List<String> userIds, Pageable pageable);
    
    @Query("{ $and: [ " +
           "{ $or: [ { privacy: 'PUBLIC' }, { userId: ?0 } ] }, " +
           "{ content: { $regex: ?1, $options: 'i' } }, " +
           "{ userId: { $nin: ?2 } } " +
           "] }")
    Page<Post> searchPublicPosts(String currentUserId, String keyword, List<String> excludedUserIds, Pageable pageable);
}
