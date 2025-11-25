package com.tien.groupservice.repository;

import com.tien.groupservice.entity.Group;
import com.tien.groupservice.entity.GroupPrivacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {
	Page<Group> findByOwnerId(String ownerId, Pageable pageable);

	Page<Group> findByPrivacy(GroupPrivacy privacy, Pageable pageable);

	Optional<Group> findByIdAndOwnerId(String id, String ownerId);

	@Query("{ $or: [ { name: { $regex: ?0, $options: 'i' } }, { description: { $regex: ?0, $options: 'i' } } ] }")
	Page<Group> searchGroups(String keyword, Pageable pageable);

	@Query("{ $and: [ " + "{ privacy: { $in: ?0 } }, "
			+ "{ $or: [ { name: { $regex: ?1, $options: 'i' } }, { description: { $regex: ?1, $options: 'i' } } ] } "
			+ "] }")
	Page<Group> searchGroupsWithPrivacy(List<GroupPrivacy> privacyTypes, String keyword, Pageable pageable);

	Page<Group> findByPrivacyIn(List<GroupPrivacy> privacyTypes, Pageable pageable);
}

