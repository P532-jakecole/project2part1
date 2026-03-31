package com.project2.Decorator;

import com.project2.Factory.Order;
import com.project2.OrderAccess;
import com.project2.Strategy.TriagingEngine;

public class BaseOrderHandler implements OrderProcess{


    public BaseOrderHandler() {}

    @Override
    public String process(Order order) {return null;}
}
