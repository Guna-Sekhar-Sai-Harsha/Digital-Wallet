package com.hpe.NotificationService.repository;

import com.hpe.NotificationService.dto.EmailDetails;
import com.hpe.NotificationService.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserName(String userName);

    List<Notification> findAllBySent(boolean sent);

    List<Notification> findAllByUserNameAndSent(String userName, boolean sent);


}
