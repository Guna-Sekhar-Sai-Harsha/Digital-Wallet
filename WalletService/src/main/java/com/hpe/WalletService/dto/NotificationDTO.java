package com.hpe.WalletService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private String userName;
    private String message;
    private String type;
    @JdbcTypeCode(SqlTypes.JSON)
    private EmailDetails email;
}
