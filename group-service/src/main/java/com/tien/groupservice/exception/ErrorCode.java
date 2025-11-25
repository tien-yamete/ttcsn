package com.tien.groupservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
	UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),
	UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
	UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
	GROUP_NOT_FOUND(2001, "Group not found", HttpStatus.NOT_FOUND),
	GROUP_ALREADY_EXISTS(2002, "Group already exists", HttpStatus.BAD_REQUEST),
	GROUP_NOT_OWNER(2003, "You are not the owner of this group", HttpStatus.FORBIDDEN),
	GROUP_NAME_REQUIRED(2004, "Group name is required", HttpStatus.BAD_REQUEST),
	MEMBER_NOT_FOUND(2005, "Member not found in group", HttpStatus.NOT_FOUND),
	MEMBER_ALREADY_EXISTS(2006, "Member already exists in group", HttpStatus.BAD_REQUEST),
	MEMBER_CANNOT_REMOVE_OWNER(2007, "Cannot remove group owner", HttpStatus.BAD_REQUEST),
	INVALID_ROLE(2008, "Invalid member role", HttpStatus.BAD_REQUEST),
	CANNOT_CHANGE_OWNER_ROLE(2009, "Cannot change owner role", HttpStatus.BAD_REQUEST),
	JOIN_REQUEST_NOT_FOUND(2010, "Join request not found", HttpStatus.NOT_FOUND),
	JOIN_REQUEST_ALREADY_EXISTS(2011, "Join request already exists", HttpStatus.BAD_REQUEST),
	ALREADY_MEMBER(2012, "User is already a member of this group", HttpStatus.BAD_REQUEST),
	INSUFFICIENT_PERMISSION(2013, "You do not have sufficient permissions", HttpStatus.FORBIDDEN),
	CANNOT_JOIN_GROUP(2014, "Cannot join this group", HttpStatus.BAD_REQUEST),
	POSTING_NOT_ALLOWED(2015, "Posting is not allowed in this group", HttpStatus.FORBIDDEN),
	;

	ErrorCode(int code, String message, HttpStatusCode statusCode) {
		this.code = code;
		this.message = message;
		this.statusCode = statusCode;
	}

	private final int code;
	private final String message;
	private final HttpStatusCode statusCode;
}

