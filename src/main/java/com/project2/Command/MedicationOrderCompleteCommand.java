package com.project2.Command;

import com.project2.Decorator.*;
import com.project2.Factory.Order;
import com.project2.OrderAccess;
import com.project2.Strategy.TriagingEngine;

public class MedicationOrderCompleteCommand implements Command {
    private final NotificationService notificationService;
    private final CommandLog commandLog;
    private final Order order;
    private final OrderAccess orderAccess;
    private final TriagingEngine triagingEngine;

    public MedicationOrderCompleteCommand(Order order, NotificationService notificationService, CommandLog commandLog, OrderAccess orderAccess, TriagingEngine triagingEngine) {
        this.order = order;
        this.notificationService = notificationService;
        this.commandLog = commandLog;
        this.orderAccess = orderAccess;
        this.triagingEngine = triagingEngine;
    }

    @Override
    public void execute(String actor) {
        String commandType = "complete";
        OrderProcess orderProcess = new OrderValidation(new OrderLogging(new BaseOrderHandler(), notificationService, commandLog, actor, commandType), notificationService, commandType);
        String errorMessage = orderProcess.process(order);
        if(errorMessage == null) {
            order.setStatus("COMPLETED");
            orderAccess.removeOrder(order);
        }
    }

    @Override
    public void undo(String actor) {
        String commandType = "undo";
        OrderProcess orderProcess = new OrderLogging(new BaseOrderHandler(), notificationService, commandLog, actor, commandType);
        String errorMessage = orderProcess.process(order);
        if(errorMessage == null) {
            order.setStatus("IN_PROGRESS");
        }
    }
}
