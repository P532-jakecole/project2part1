package com.project2.Command;

import com.project2.Decorator.*;
import com.project2.Factory.Order;

public class LabOrderClaimCommand implements Command {
    private final NotificationService notificationService;
    private final CommandLog commandLog;
    private final Order order;

    public LabOrderClaimCommand(Order order, NotificationService notificationService, CommandLog commandLog) {
        this.order = order;
        this.notificationService = notificationService;
        this.commandLog = commandLog;
    }

    @Override
    public void execute(String actor) {
        String commandType = "claim";
        OrderProcess orderProcess = new OrderValidation(new OrderLogging(new BaseOrderHandler(), notificationService, commandLog, actor, commandType), notificationService, commandType);
        String errorMessage = orderProcess.process(order);
        if(errorMessage == null) {
            order.setStatus("IN_PROGRESS");
        }
    }
}
