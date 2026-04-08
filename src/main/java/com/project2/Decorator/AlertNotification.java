package com.project2.Decorator;

import com.project2.Factory.Order;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class AlertNotification implements NotificationService {
    public final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private Integer badgeCount = 0;

    public AlertNotification(NotificationService notificationService, SimpMessagingTemplate messagingTemplate) {
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void update(Order order, String event) {
        notify(order, event);
    }

    @Override
    public void notify(Order order, String event) {

        System.out.println("Badge count: " + badgeCount);
        switch (event.toLowerCase()) {
            case "cancel error":
                notificationService.notify(order, event);
                return;
            default:
                badgeCount++;
        }

        messagingTemplate.convertAndSend("/order/alert",
                badgeCount);

        notificationService.notify(order, event);
    }
}
