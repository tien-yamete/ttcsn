package com.tien.notificationservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tien.notificationservice.dto.request.EmailRequest;
import com.tien.notificationservice.dto.request.SendEmailRequest;
import com.tien.notificationservice.dto.request.Sender;
import com.tien.notificationservice.dto.response.EmailResponse;
import com.tien.notificationservice.exception.AppException;
import com.tien.notificationservice.exception.ErrorCode;
import com.tien.notificationservice.repository.httpclient.EmailClient;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    EmailClient emailClient;

    @Value("${notification.email.brevo-apikey}")
    @NonFinal
    String apiKey;

    public EmailResponse sendEmail(SendEmailRequest request) {
        if (request == null) {
            log.error("SendEmailRequest is null");
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
        
        if (request.getTo() == null || request.getTo().getEmail() == null || request.getTo().getEmail().isEmpty()) {
            log.error("Recipient email is null or empty");
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
        
        if (apiKey == null || apiKey.isEmpty()) {
            log.error("Brevo API key is not configured");
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
        
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("Friendify")
                        .email("tavantien786@gmail.com")
                        .build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(request.getHtmlContent())
                .build();
        
        log.info("Sending email to: {}, subject: {}", request.getTo().getEmail(), request.getSubject());
        
        try {
            EmailResponse response = emailClient.sendEmail(apiKey, emailRequest);
            log.info("Email sent successfully to: {}", request.getTo().getEmail());
            return response;
        } catch (FeignException e) {
            log.error("Failed to send email to: {}. Status: {}, Message: {}, Response body: {}", 
                    request.getTo().getEmail(), 
                    e.status(), 
                    e.getMessage(),
                    e.contentUTF8());
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        } catch (Exception e) {
            log.error("Unexpected error while sending email to: {}", request.getTo().getEmail(), e);
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }
}
