package com.project2.Decorator;

import com.project2.Command.CommandLog;
import com.project2.Factory.Order;

import java.util.Objects;

public class OrderLogging extends OrderHandler{
    private NotificationService notificationService;
    private CommandLog commandLog;
    private String actor;
    private String commandType;

    public OrderLogging(OrderProcess orderProcess, NotificationService notificationService, CommandLog commandLog, String actor, String commandType) {
        super(orderProcess);
        this.notificationService = notificationService;
        this.commandLog = commandLog;
        this.actor = actor;
        this.commandType = commandType;
    }

    @Override
    public String process(Order order) {
        String escalationInfo = super.process(order);
        if(Objects.equals(order.getPriority(), "STAT")){
            if(escalationInfo != null){
                String[] info = escalationInfo.split(",");
                String priorStatus = info[0];
                String amountEffected = info[1];
                commandLog.addLog(order, actor, commandType, priorStatus, amountEffected);
            }else{
                commandLog.addLog(order, actor, commandType);
            }
        }else{
            commandLog.addLog(order, actor, commandType);
        }
        return null;
    }
}