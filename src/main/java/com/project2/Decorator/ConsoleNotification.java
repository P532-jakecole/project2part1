package com.project2.Decorator;

import com.project2.Factory.Order;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class ConsoleNotification implements NotificationService{
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    public ConsoleNotification(NotificationService notificationService, SimpMessagingTemplate messagingTemplate) {
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void update(Order order, String event) {
        notify(order, event);
    }

    @Override
    public void notify(Order order, String event) {
        String message = null;
        switch (event.toLowerCase()) {
            case "submit":
                //TODO: Change message
                message = "Order Submitted";
                break;
            case "claim":
                //TODO: Change message
                message = "Order Claimed";
                break;
            case "complete":
                //TODO: Change message
                message = "Order Complete";
                break;
            case "cancel":
                //TODO: Change message
                message = "Order Cancelled";
                break;
            case "cancel error":
                //TODO: Change message
                message = "Error cancelling order";
                break;
        }
        if (message != null) {
            messagingTemplate.convertAndSend("/order/logs",
                    message);
        }
        notificationService.notify(order, event);
    }
}
