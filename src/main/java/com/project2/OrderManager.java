package com.project2;

import com.project2.Command.*;
import com.project2.Decorator.NotificationService;
import com.project2.Factory.LabOrder;
import com.project2.Factory.Order;
import com.project2.Factory.OrderFactory;
import com.project2.Strategy.TriagingEngine;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderManager{

    private final OrderFactory orderFactory;
    private final TriagingEngine triagingEngine;
    private final OrderAccess orderAccess;
    private final NotificationService notificationService;
    private final CommandLog commandLog;


    public OrderManager(OrderFactory orderFactory, TriagingEngine triagingEngine, OrderAccess orderAccess, CommandLog commandLog, NotificationService notificationService) {
        this.orderFactory = orderFactory;
        this.triagingEngine = triagingEngine;
        this.orderAccess = orderAccess;
        this.commandLog = commandLog;
        this.notificationService = notificationService;
    }

    public void createOrder(String[] order) {
        String actor = order[2];
        Order newOrder = orderFactory.create(order);
        Command create = commandLog.getSubmitCommand(newOrder.getOrderID());
        create.execute(actor);
    }

    public void cancelOrder(int orderId, String clinician) {
        Command cancel = commandLog.getCancelCommand(orderId);
        cancel.execute(clinician);
    }

    public void completeOrder(int orderId, String staffMember) {
        Command complete = commandLog.getCompleteCommand(orderId);
        complete.execute(staffMember);
    }

    public void claimOrder(int orderId, String staffMember) {
        Command claim = commandLog.getClaimCommand(orderId);
        claim.execute(staffMember);
    }

    public List<Order> findAll(){
        return orderAccess.listPendingOrders();
    }

    public void updateTriage(String triage){
        triagingEngine.updateTriage(triage);
    }

    public ArrayList<String[]> getCommandLog(){
        return commandLog.getCommandLog();
    }

    public Order getNextOrder(){
        return orderAccess.getNextOrder();
    }

    public Order getNextOrder(String staff){
        return orderAccess.getNextOrder(staff);
    }
}
