package com.tien.groupservice.mapper;

import com.tien.groupservice.dto.response.GroupMemberResponse;
import com.tien.groupservice.entity.GroupMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GroupMemberMapper {
	@Mapping(target = "username", ignore = true)
	@Mapping(target = "avatar", ignore = true)
	@Mapping(target = "joinedDate", ignore = true)
	GroupMemberResponse toGroupMemberResponse(GroupMember groupMember);
}

