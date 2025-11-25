package com.tien.groupservice.repository;

import com.tien.groupservice.entity.JoinRequest;
import com.tien.groupservice.entity.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JoinRequestRepository extends MongoRepository<JoinRequest, String> {
	Optional<JoinRequest> findByGroupIdAndUserId(String groupId, String userId);

	boolean existsByGroupIdAndUserId(String groupId, String userId);

	Page<JoinRequest> findByGroupId(String groupId, Pageable pageable);

	Page<JoinRequest> findByGroupIdAndStatus(String groupId, RequestStatus status, Pageable pageable);

	Page<JoinRequest> findByUserId(String userId, Pageable pageable);

	void deleteByGroupIdAndUserId(String groupId, String userId);

	void deleteAllByGroupId(String groupId);
}

