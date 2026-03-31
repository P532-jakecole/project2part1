package com.project2.Observer;

import com.project2.Factory.Order;

public interface Observer {
    void update(Order order, String event);
}
