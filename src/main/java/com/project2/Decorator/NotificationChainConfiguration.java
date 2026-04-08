package com.project2.Decorator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
public class NotificationChainConfiguration {

    @Bean
    public NotificationChain notificationChain(SimpMessagingTemplate simpMessagingTemplate) {
        NotificationService base = new BaseNotification();
        NotificationService chain = new ConsoleNotification(base, simpMessagingTemplate);

        NotificationChain notifications = new NotificationChain(simpMessagingTemplate);
        notifications.setActiveNotifications(chain);
        return notifications;
    }
}
