package com.project2.Decorator;

import com.project2.Factory.Order;

public abstract class OrderHandler implements OrderProcess{
    public OrderProcess orderProcess;

    public OrderHandler(OrderProcess orderProcess) {
        this.orderProcess = orderProcess;
    }

    @Override
    public String process(Order order) {
        return orderProcess.process(order);
    }
}
