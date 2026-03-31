package com.project2.Command;

import com.project2.Decorator.BaseOrderHandler;
import com.project2.Decorator.OrderLogging;
import com.project2.Decorator.OrderProcess;
import com.project2.Decorator.OrderValidation;
import com.project2.Factory.Order;
import com.project2.Factory.OrderFactory;
import com.project2.Decorator.NotificationService;
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
        OrderProcess orderProcess = new OrderValidation(new OrderLogging(new BaseOrderHandler(), notificationService, commandLog, actor, commandType), notificationService, commandType);
        String errorMessage = orderProcess.process(order);
        if(errorMessage == null) {
            order.setStatus("PENDING");
            int position = triagingEngine.getPosition(order.getPriority(), order.getTimestamp());
            orderAccess.saveOrder(position, order);
        }
    }
}
