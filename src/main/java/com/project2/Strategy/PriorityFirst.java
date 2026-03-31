package com.project2.Strategy;

import com.project2.Factory.Order;
import com.project2.OrderAccess;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PriorityFirst implements TriageStrategy{
    private final OrderAccess orderAccess;

    public PriorityFirst(OrderAccess orderAccess) {
        this.orderAccess = orderAccess;
    }

    private int priorityValue(String priority){
        switch (priority.toLowerCase()){
            case "stat":
                return 3;
            case "urgent":
                return 2;
            case "routine":
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public int getPosition(String priority, LocalDateTime timestamp) {
        ArrayList<Order> pending = orderAccess.listPendingOrders();
        int count = 0;
        int orderPriority = priorityValue(priority);
        for(Order order : pending) {
            int pendingPriority = priorityValue(order.getPriority());

            if(pendingPriority > orderPriority) {
                count++;
            }else if(pendingPriority == orderPriority){
                if(order.getTimestamp().isBefore(timestamp)){
                    count++;
                }
            }
        }
        return count;
    }
}
