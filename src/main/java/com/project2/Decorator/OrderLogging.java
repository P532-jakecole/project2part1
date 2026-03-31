package com.project2.Decorator;

import com.project2.Command.CommandLog;
import com.project2.Factory.Order;

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
        //notificationService.notify(order, commandType);
        commandLog.addLog(order, actor, commandType);
        super.process(order);
        return null;
    }
}