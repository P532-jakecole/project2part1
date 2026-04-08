package com.project2.Decorator;

import com.project2.Command.CommandLog;
import com.project2.Factory.Order;

import java.util.Objects;

public class PriorityEscalation extends OrderHandler{
    private NotificationService notificationService;
    private CommandLog commandLog;

    public PriorityEscalation(OrderProcess orderProcess, CommandLog commandLog) {
        super(orderProcess);
        this.commandLog = commandLog;
    }

    @Override
    public String process(Order order) {
        super.process(order);
        if(Objects.equals(order.getPriority(), "STAT")){
            commandLog.setEscalation(order.getType());
            commandLog.setEffected(order.getType(), order.getOrderID());
            return "No Change,0";
        }
        boolean escalation = commandLog.getEscalation(order.getType());
        if(escalation && Objects.equals(order.getPriority(), "URGENT")){
            order.setPriority("STAT");
            commandLog.addEffected(order.getType());
            return "Escalated,N/A";
        }
        return null;
    }
}
