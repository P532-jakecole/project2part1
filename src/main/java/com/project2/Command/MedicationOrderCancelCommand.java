package com.project2.Command;

import com.project2.Decorator.*;
import com.project2.Factory.Order;
import com.project2.OrderAccess;
import com.project2.Strategy.TriagingEngine;

public class MedicationOrderCancelCommand implements Command {
    private final OrderAccess orderAccess;
    private final NotificationService notificationService;
    private final CommandLog commandLog;
    private final Order order;
    private final TriagingEngine triagingEngine;
    private String status;

    public MedicationOrderCancelCommand(Order order, OrderAccess orderAccess, NotificationService notificationService, CommandLog commandLog, TriagingEngine triagingEngine) {
        this.orderAccess = orderAccess;
        this.order = order;
        this.notificationService = notificationService;
        this.commandLog = commandLog;
        this.triagingEngine = triagingEngine;
        status = "";
    }
    @Override
    public void execute(String actor) {
        status = order.getStatus();
        String commandType = "cancel";
        OrderProcess orderProcess = new OrderValidation(new OrderLogging(new BaseOrderHandler(), notificationService, commandLog, actor, commandType), notificationService, commandType);
        String errorMessage = orderProcess.process(order);
        if(errorMessage == null) {
            order.setStatus("CANCELLED");
            orderAccess.removeOrder(order);
        }
    }

    @Override
    public void undo(String actor) {
        String commandType = "undo";
        OrderProcess orderProcess = new OrderValidation(new OrderLogging( new PriorityEscalation(new BaseOrderHandler(), commandLog), notificationService, commandLog, actor, commandType), notificationService, commandType);
        String errorMessage = orderProcess.process(order);
        if(errorMessage == null) {
            order.setStatus(status);
            int position = triagingEngine.getPosition(order.getPriority(), order.getTimestamp(), order.getType());
            orderAccess.saveOrder(position, order);
        }
    }
}
