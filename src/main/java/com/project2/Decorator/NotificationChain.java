package com.project2.Decorator;

import com.project2.Factory.Order;
import org.springframework.stereotype.Component;

@Component
public class NotificationChain implements NotificationService {
    private NotificationService activeNotifications;

    public void setActiveNotifications(NotificationService activeNotifications) {
        this.activeNotifications = activeNotifications;
    }

    public NotificationService getActiveNotifications() {
        return activeNotifications;
    }

    @Override
    public void notify(Order order, String event) {
        activeNotifications.notify(order, event);
    }

    @Override
    public void update(Order order, String event) {
        notify(order, event);
    }
}
