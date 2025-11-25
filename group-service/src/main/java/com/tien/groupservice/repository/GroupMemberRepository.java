package com.tien.groupservice.repository;

import com.tien.groupservice.entity.GroupMember;
import com.tien.groupservice.entity.MemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends MongoRepository<GroupMember, String> {
	Optional<GroupMember> findByGroupIdAndUserId(String groupId, String userId);

	boolean existsByGroupIdAndUserId(String groupId, String userId);

	Page<GroupMember> findByGroupId(String groupId, Pageable pageable);

	Page<GroupMember> findByGroupIdAndRole(String groupId, MemberRole role, Pageable pageable);

	Page<GroupMember> findByUserId(String userId, Pageable pageable);

	List<GroupMember> findByGroupId(String groupId);

	List<GroupMember> findByUserId(String userId);

	long countByGroupId(String groupId);

	void deleteByGroupIdAndUserId(String groupId, String userId);

	void deleteAllByGroupId(String groupId);
}

