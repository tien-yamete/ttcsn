package com.tien.socialservice.repository;

import com.tien.socialservice.dto.PageResponse;
import com.tien.socialservice.entity.Friendship;
import com.tien.socialservice.entity.FriendshipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, String> {
    Optional<Friendship> findByUserIdAndFriendId(String userId, String friendId);

    Optional<Friendship> findByUserIdAndFriendIdAndStatus(String userId, String friendId, FriendshipStatus status);

    boolean existsByUserIdAndFriendId(String userId, String friendId);

    @Query("SELECT f FROM Friendship f WHERE (f.userId = :userId OR f.friendId = :userId) AND f.status = :status")
    Page<Friendship> findFriendshipsByUserIdAndStatus(
            @Param("userId") String userId, @Param("status") FriendshipStatus status, Pageable pageable);

    @Query("SELECT f FROM Friendship f WHERE f.userId = :userId AND f.status = :status")
    Page<Friendship> findSentFriendRequests(@Param("userId") String userId, @Param("status") FriendshipStatus status, Pageable pageable);

    @Query("SELECT f FROM Friendship f WHERE f.friendId = :userId AND f.status = :status")
    Page<Friendship> findReceivedFriendRequests(@Param("userId") String userId, @Param("status") FriendshipStatus status, Pageable pageable);

    @Query("SELECT f FROM Friendship f WHERE (f.userId = :userId OR f.friendId = :userId) AND f.status = 'ACCEPTED'")
    List<Friendship> findAllFriends(@Param("userId") String userId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friendship f " +
            "WHERE ((f.userId = :userId1 AND f.friendId = :userId2) OR (f.userId = :userId2 AND f.friendId = :userId1)) " +
            "AND f.status = 'ACCEPTED'")
    boolean areFriends(@Param("userId1") String userId1, @Param("userId2") String userId2);

    @Modifying
    @Transactional
    @Query("DELETE FROM Friendship f WHERE (f.userId = :userId AND f.friendId = :friendId) OR (f.userId = :friendId AND f.friendId = :userId)")
    void deleteFriendship(@Param("userId") String userId, @Param("friendId") String friendId);
}
