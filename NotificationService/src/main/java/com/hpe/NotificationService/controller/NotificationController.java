package com.hpe.NotificationService.controller;



import com.hpe.NotificationService.dto.NotificationDTO;
import com.hpe.NotificationService.dto.NotificationResponse;
import com.hpe.NotificationService.model.Notification;
import com.hpe.NotificationService.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notify")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @KafkaListener(topics = "Notification", groupId = "notificationGroup")
    public void createNotification(String notificationDTO){
        notificationService.createNotification(notificationDTO);
    }

    @GetMapping("/sent/user/{userName}")
    public ResponseEntity<List<Notification>> getAllNotificationsByUserName(@PathVariable String userName){
        return notificationService.getAllNotificationsByUserName(userName);
    }

    @GetMapping("/sent/{userName}/{sent}")
    public ResponseEntity<List<Notification>> getAllByUserNameAndSent(@PathVariable String userName, @PathVariable boolean sent){
        return notificationService.getAllByUserNameAndSent(userName, sent);
    }

    @GetMapping("/sent/{sent}")
    public ResponseEntity<List<Notification>> getAllBySent(@PathVariable boolean sent){
        return notificationService.getAllBySent(sent);
    }

    @PostMapping("/sent")
    public ResponseEntity<NotificationResponse> markAsSent(Long id){
        return notificationService.markAsSent(id);
    }
}
