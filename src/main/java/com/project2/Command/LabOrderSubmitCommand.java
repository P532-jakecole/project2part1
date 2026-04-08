package com.project2.Command;

import com.project2.Decorator.*;
import com.project2.Factory.Order;
import com.project2.Factory.OrderFactory;
import com.project2.OrderAccess;
import com.project2.Strategy.TriagingEngine;

public class LabOrderSubmitCommand implements Command {
    private TriagingEngine triagingEngine;
    private OrderAccess orderAccess;
    private NotificationService notificationService;
    private CommandLog commandLog;
    private Order order;

    public LabOrderSubmitCommand(Order order, TriagingEngine triagingEngine, OrderAccess orderAccess, NotificationService notificationService, CommandLog commandLog) {
        this.triagingEngine = triagingEngine;
        this.orderAccess = orderAccess;
        this.notificationService = notificationService;
        this.commandLog = commandLog;
        this.order = order;
    }

    @Override
    public void execute(String actor) {
        String commandType = "submit";
        OrderProcess orderProcess = new OrderValidation(new OrderLogging( new PriorityEscalation(new BaseOrderHandler(), commandLog), notificationService, commandLog, actor, commandType), notificationService, commandType);
        String errorMessage = orderProcess.process(order);
        if(errorMessage == null) {
            order.setStatus("PENDING");
            int position = triagingEngine.getPosition(order.getPriority(), order.getTimestamp(), order.getType());
            orderAccess.saveOrder(position, order);
        }
    }

    @Override
    public void undo(String actor) {
        String commandType = "undo";
        OrderProcess orderProcess = new OrderLogging(new BaseOrderHandler(), notificationService, commandLog, actor, commandType);
        String errorMessage = orderProcess.process(order);
        if(errorMessage == null){
            orderAccess.removeOrder(order);
            notificationService.notify(order, "cancel");
        }
    }
}
