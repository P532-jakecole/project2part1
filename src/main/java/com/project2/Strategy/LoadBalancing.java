package com.project2.Strategy;

import com.project2.Factory.Order;
import com.project2.OrderAccess;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class LoadBalancing implements TriageStrategy{
    private final OrderAccess orderAccess;

    public LoadBalancing(OrderAccess orderAccess) {
        this.orderAccess = orderAccess;
    }

    @Override
    public int getPosition(String priority, LocalDateTime timestamp,String type) {
        ArrayList<Order> pending = orderAccess.listPendingOrders();
        return pending.size();
    }

    @Override
    public void reorder() {
        ArrayList<Order> pending = orderAccess.listPendingOrders();
        pending.sort(Comparator
                .comparing((Order order) -> order.getTimestamp())
        );
        orderAccess.setPendingOrders(pending);
    }
}
