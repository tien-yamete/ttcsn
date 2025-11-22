package com.tien.chatservice.mapper;

import com.tien.chatservice.dto.response.ReadReceiptResponse;
import com.tien.chatservice.entity.ReadReceipt;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReadReceiptMapper {
    ReadReceiptResponse toReadReceiptResponse(ReadReceipt readReceipt);
}
