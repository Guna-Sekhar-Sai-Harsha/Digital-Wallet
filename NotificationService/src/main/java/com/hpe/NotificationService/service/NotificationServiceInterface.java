package com.hpe.NotificationService.service;


import com.hpe.NotificationService.dto.EmailDetails;
import com.hpe.NotificationService.dto.NotificationDTO;
import com.hpe.NotificationService.dto.NotificationResponse;
import com.hpe.NotificationService.model.Notification;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface NotificationServiceInterface {

    /*
        1. Creates a Notification in DB
        2. Triggers send based on type of notification
        3. Input parameter is of type NotificationDTO
        4. Logs the Notification Response
     */
    void createNotification(String notificationDTO);

    /*
        1. Sends an Email to a recipient and sets the Notification sent status as True.
        2. FromAddress is sent in Application.properties
        3. Triggered by createNotification()
        4. Inputs are of type EmailResponse and NotificationID.
    */
    void sendEmailAlert(EmailDetails emailResponse, Long id);

    /*
        1. Sets the Notification sent status as True.
        2. Triggered by sendEmailAlert() [In future, modified with push notifications]
        3. Input parameter is of NotificationID.
        4. Returns data type of Notification Response.
     */
    ResponseEntity<NotificationResponse> markAsSent(Long id);

    /*
        1. Returns list of Notifications sent to a user
        2. Input parameter is Username
     */
    ResponseEntity<List<Notification>> getAllNotificationsByUserName(String userName);

    /*
        1. Returns list of Notifications sent to a user and by sent status.
        2. Input parameter is Username and sent status (boolean).
     */
    ResponseEntity<List<Notification>> getAllByUserNameAndSent(String userName, boolean sent);

    /*
         1. Returns list of Notifications sent to all users by sent status.
      */
    ResponseEntity<List<Notification>> getAllBySent(boolean sent);


}
