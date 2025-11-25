package com.tien.chatservice.mapper;

import org.mapstruct.Mapper;

import com.tien.chatservice.dto.response.ReadReceiptResponse;
import com.tien.chatservice.entity.ReadReceipt;

@Mapper(componentModel = "spring")
public interface ReadReceiptMapper {
    ReadReceiptResponse toReadReceiptResponse(ReadReceipt readReceipt);
}
