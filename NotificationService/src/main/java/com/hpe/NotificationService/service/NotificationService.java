package com.hpe.NotificationService.service;


import com.google.gson.Gson;
import com.hpe.NotificationService.dto.EmailDetails;
import com.hpe.NotificationService.dto.NotificationDTO;
import com.hpe.NotificationService.dto.NotificationResponse;
import com.hpe.NotificationService.model.Notification;
import com.hpe.NotificationService.repository.NotificationRepository;
import com.hpe.NotificationService.utils.NotificationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationService implements NotificationServiceInterface{

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String senderMail;

    @Override
    public void createNotification(String notificationDTO){


        try {
            Gson gson = new Gson();
            NotificationDTO notificationDTO1 = gson.fromJson(notificationDTO, NotificationDTO.class);
            Notification notification = new Notification();
            notification.setUserName(notificationDTO1.getUserName());
            notification.setMessage(notificationDTO1.getMessage());
            notification.setType(notificationDTO1.getType());
            notification.setSent(false);
            notification.setCreatedAt(LocalDateTime.now());
            Notification savednotification = notificationRepository.save(notification);

            /*
                Logic to send notifications
            */

            if(notificationDTO1.getType().equals("EMail")){
                sendEmailAlert(notificationDTO1.getEmail(), savednotification.getId());
            }

            log.info("Notification successfully created with response {}", NotificationResponse.builder()
                    .responseCode(NotificationUtils.NOTIFICATION_CREATED_CODE)
                    .responseMessage(NotificationUtils.NOTIFICATION_CREATED_MESSAGE)
                    .notification(savednotification)
                    .build());

        }catch (Exception e){
            System.out.println(e);
            log.info("Error while creating Notification: {}", NotificationResponse.builder()
                    .responseCode(NotificationUtils.NOTIFICATION_CREATED_ERROR_CODE)
                    .responseMessage(NotificationUtils.NOTIFICATION_CREATED_ERROR_MESSAGE)
                    .notification(null)
                    .build());
            log.info("The error is: {}", e);
        }
    }

    @Override
    public void sendEmailAlert(EmailDetails email, Long id){
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderMail);
            mailMessage.setTo(email.getRecipient());
            mailMessage.setSubject(email.getSubject());
            mailMessage.setText(email.getMessageBody());
            javaMailSender.send(mailMessage);
            markAsSent(id);
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<NotificationResponse> markAsSent(Long id){
        Notification notification = notificationRepository.getReferenceById(id);
        notification.setSent(true);
        Notification savednotification = notificationRepository.save(notification);
        return new ResponseEntity<>(NotificationResponse.builder()
                .responseCode(NotificationUtils.NOTIFICATION_SENT_CODE)
                .responseMessage(NotificationUtils.NOTIFICATION_SENT_MESSAGE)
                .notification(savednotification)
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Notification>> getAllNotificationsByUserName(String userName){

        return new ResponseEntity<>(notificationRepository.findAllByUserName(userName), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<List<Notification>> getAllBySent(boolean sent){
        return new ResponseEntity<>(notificationRepository.findAllBySent(sent), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Notification>> getAllByUserNameAndSent(String userName, boolean sent){
        return new ResponseEntity<>(notificationRepository.findAllByUserNameAndSent(userName,sent), HttpStatus.OK);
    }

}
