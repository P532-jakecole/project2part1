package com.project2.Decorator;

import com.project2.Factory.Order;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class EmailNotification implements NotificationService{
    public final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    public EmailNotification(NotificationService notificationService, SimpMessagingTemplate messagingTemplate) {
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
            case "pending":
                message = String.format("Mock Email: Dear %s, \nThe order with id %d was successfully submitted\n", order.getClinicianName(), order.getOrderID());
                break;
            case "in_progress":
                message = String.format("Mock Email: The order with an id of %d was successfully claimed.\n", order.getOrderID());
                break;
            case "completed":
                message = String.format("Mock Email: The order with an id of %d was successfully completed.\n", order.getOrderID());
                break;
            case "cancelled":
                message = String.format("Mock Email: The order with an id of %d was successfully cancelled.\n", order.getOrderID());
                break;
            case "cancel error":
                message = String.format("Mock Email: Dear %s,\nThere was an error when trying to cancel order number %d. The current status of order is %s\n",order.getClinicianName(), order.getOrderID(), order.getStatus());
                break;
        }
        if (message != null) {
            messagingTemplate.convertAndSend("/order/logs",
                    message);
        }
        notificationService.notify(order, event);
    }
}
