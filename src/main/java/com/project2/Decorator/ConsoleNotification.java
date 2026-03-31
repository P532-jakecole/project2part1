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
        System.out.println("Notification Input");
        System.out.println(event);
        switch (event.toLowerCase()) {
//            case "submit":
//                message = String.format("Order %d Successfully Submitted by %s\n", order.getOrderID(), order.getClinicianName());
//                break;
//            case "claim":
//                message = String.format("Order %d Successfully Claimed\n", order.getOrderID());
//                break;
//            case "complete":
//                message = String.format("Order %d Successfully Completed\n", order.getOrderID());
//                break;
//            case "cancel":
//                message = String.format("Order %d Successfully Cancelled\n", order.getOrderID());
//                break;
            case "pending":
                message = String.format("Order %d Successfully Submitted by %s\n", order.getOrderID(), order.getClinicianName());
                break;
            case "in_progress":
                message = String.format("Order %d Successfully Claimed\n", order.getOrderID());
                break;
            case "completed":
                message = String.format("Order %d Successfully Completed\n", order.getOrderID());
                break;
            case "cancelled":
                message = String.format("Order %d Successfully Cancelled\n", order.getOrderID());
                break;
            case "cancel error":
                message = String.format("Error cancelling order %d. Status of order is %s\n", order.getOrderID(), order.getStatus());
                break;
        }
        if (message != null) {
            System.out.println("Message Sent");
            messagingTemplate.convertAndSend("/order/logs",
                    message);
        }
        notificationService.notify(order, event);
    }
}
