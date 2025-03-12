package com.hpe.NotificationService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDetails {

    private String recipient;
    private String subject;
    private String messageBody;
    private String attachment;

}
