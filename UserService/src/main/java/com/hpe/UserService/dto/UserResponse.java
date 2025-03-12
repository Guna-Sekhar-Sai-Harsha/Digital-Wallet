package com.hpe.UserService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String responseCode;
    private String responseMessage;
    private String username;
}
