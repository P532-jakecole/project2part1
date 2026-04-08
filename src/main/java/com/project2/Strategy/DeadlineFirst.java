package com.project2.Strategy;

import com.project2.Factory.Order;
import com.project2.OrderAccess;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class DeadlineFirst implements TriageStrategy{
    private final OrderAccess orderAccess;

    public DeadlineFirst(OrderAccess orderAccess) {
        this.orderAccess = orderAccess;
    }

    private LocalDateTime getDeadline(String priority, LocalDateTime timestamp, String type) {
        switch (priority.toLowerCase()) {
            case "stat":
                if(type.equalsIgnoreCase("lab")){
                    return timestamp.plusMinutes(30);
                }
                return timestamp.plusMinutes(40);
            case "urgent":
                return timestamp.plusMinutes(60);
            case "routine":
                return timestamp.plusMinutes(90);
            default:
                return timestamp;
        }
    }

    @Override
    public int getPosition(String priority, LocalDateTime timestamp, String type) {
        LocalDateTime deadline = getDeadline(priority, timestamp, type);
        ArrayList<Order> pending = orderAccess.listPendingOrders();
        int count = 0;
        for(Order order : pending) {
            if(deadline.isBefore(getDeadline(order.getPriority(), order.getTimestamp(), order.getType()))) {
                return count;
            }
            count++;
        }
        return count;
    }

    @Override
    public void reorder() {
        ArrayList<Order> pending = orderAccess.listPendingOrders();
        pending.sort(Comparator
                .comparing((Order order) -> getDeadline(order.getPriority(), order.getTimestamp(), order.getType()))
        );
        orderAccess.setPendingOrders(pending);
    }
}