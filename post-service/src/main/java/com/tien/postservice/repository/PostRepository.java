package com.tien.postservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.tien.postservice.entity.Post;
import com.tien.postservice.entity.PrivacyType;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    Page<Post> findAllByUserId(String userId, Pageable pageable);

    Page<Post> findByPrivacyAndUserIdNotIn(PrivacyType privacy, List<String> userIds, Pageable pageable);

    Page<Post> findByUserIdIn(List<String> userIds, Pageable pageable);

    @Query("{ $and: [ " + "{ userId: { $in: ?0 } }, "
            + "{ $or: [ { privacy: 'PUBLIC' }, { privacy: 'PRIVATE', userId: ?1 } ] } "
            + "] }")
    Page<Post> findByUserIdInWithPrivacyFilter(List<String> userIds, String currentUserId, Pageable pageable);

    @Query("{ $and: [ " + "{ userId: ?0 }, "
            + "{ $or: [ { privacy: 'PUBLIC' }, { privacy: 'PRIVATE', userId: ?0 } ] } "
            + "] }")
    Page<Post> findByUserIdWithPrivacy(String userId, Pageable pageable);

    @Query("{ $and: [ " + "{ $or: [ { privacy: 'PUBLIC' }, { userId: ?0 } ] }, "
            + "{ content: { $regex: ?1, $options: 'i' } }, "
            + "{ userId: { $nin: ?2 } }, "
            + "{ groupId: null } "
            + "] }")
    Page<Post> searchPublicPosts(String currentUserId, String keyword, List<String> excludedUserIds, Pageable pageable);

    Page<Post> findByGroupId(String groupId, Pageable pageable);

    @Query("{ $and: [ " + "{ groupId: ?0 }, " + "{ $or: [ { privacy: 'PUBLIC' }, { userId: ?1 } ] } " + "] }")
    Page<Post> findByGroupIdWithPrivacy(String groupId, String currentUserId, Pageable pageable);
}
