package com.project2.Decorator;

import com.project2.Factory.Order;

public class OrderValidation extends OrderHandler{
    private final NotificationService notificationService;
    private final String commandType;

    public OrderValidation(OrderProcess orderProcess, NotificationService notificationService, String commandType) {
        super(orderProcess);
        this.notificationService = notificationService;
        this.commandType = commandType;
    }

    @Override
    public String process(Order order) {
        if(order.getTimestamp() == null){
            notificationService.notify(order, "OrderFailed");
            return "Error";
        }else if(commandType.equalsIgnoreCase("cancel")){
            if(!order.getStatus().equalsIgnoreCase("pending")) {
                notificationService.notify(order, "Cancel error");
                return "Error";
            }
            super.process(order);
            return null;
        }else{
            super.process(order);
            return null;
        }
    }
}
