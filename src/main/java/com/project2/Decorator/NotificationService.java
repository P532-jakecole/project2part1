package com.project2.Decorator;

import com.project2.Factory.Order;
import com.project2.Observer.Observer;

public interface NotificationService extends Observer {
    void notify(Order order, String event);
}
