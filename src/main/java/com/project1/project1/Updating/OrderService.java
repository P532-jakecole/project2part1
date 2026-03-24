package com.project1.project1.Updating;

import com.project1.project1.Trading.Order;
import com.project1.project1.User.Holding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendUpdate(Order order) {
        messagingTemplate.convertAndSend(
                "/trade/" + order.getName(),
                order
        );
    }

    public void sendHistory(Order history) {
        messagingTemplate.convertAndSend(
                "/trade/history",
                history
        );
    }

    public void addPending(Order order) {
        System.out.println("Adding Pending");
        messagingTemplate.convertAndSend(
                "/trade/addPending",
                order
        );
    }

    public void removePending(Order order) {
        System.out.println("Removing Pending");
        messagingTemplate.convertAndSend(
                "/trade/subPending",
                order
        );
    }

    public void addHolding(Holding hold) {
        System.out.println("Adding Holding...");
        messagingTemplate.convertAndSend(
                "/trade/addHolding",
                hold
        );
    }

    public void removeHolding(Holding hold) {
        System.out.println("Removing Holding...");
        messagingTemplate.convertAndSend(
                "/trade/subHolding",
                hold
        );
    }

    public void updateBalance(String balances) {
        System.out.println("Updating Balance...");
        messagingTemplate.convertAndSend(
                "/info/balance",
                balances
        );
    }

    public void sendNotification(String message) {
        messagingTemplate.convertAndSend(
                "/info/notify",
                message
        );
    }
}
