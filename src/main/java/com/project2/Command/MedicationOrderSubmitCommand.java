package com.project2.Command;

import com.project2.Decorator.*;
import com.project2.Factory.Order;
import com.project2.OrderAccess;
import com.project2.Strategy.TriagingEngine;

public class MedicationOrderSubmitCommand implements Command {
    private final TriagingEngine triagingEngine;
    private final OrderAccess orderAccess;
    private final NotificationService notificationService;
    private final CommandLog commandLog;
    private final Order order;

    public MedicationOrderSubmitCommand(Order order, TriagingEngine triagingEngine, OrderAccess orderAccess, NotificationService notificationService, CommandLog commandLog) {
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
        if(errorMessage == null){
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
