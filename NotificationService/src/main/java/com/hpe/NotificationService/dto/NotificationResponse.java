package com.hpe.NotificationService.dto;

import com.hpe.NotificationService.model.Notification;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {
    private String responseCode;
    private String responseMessage;
    private Notification notification;
}
