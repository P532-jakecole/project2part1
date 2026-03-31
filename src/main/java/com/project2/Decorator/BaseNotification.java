package com.project2.Decorator;

import com.project2.Factory.Order;

public class BaseNotification implements NotificationService {
    @Override
    public void notify(Order order, String event) {}

    @Override
    public void update(Order order, String event) {}
}
