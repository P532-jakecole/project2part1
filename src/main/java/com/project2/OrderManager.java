package com.project2;

import com.project2.Command.*;
import com.project2.Decorator.NotificationChain;
import com.project2.Decorator.NotificationService;
import com.project2.Factory.LabOrder;
import com.project2.Factory.Order;
import com.project2.Factory.OrderFactory;
import com.project2.Strategy.TriagingEngine;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OrderManager{

    private final OrderFactory orderFactory;
    private final TriagingEngine triagingEngine;
    private final OrderAccess orderAccess;
    private final NotificationChain notificationChain;
    private final CommandLog commandLog;

    private Command lastCommand = null;


    public OrderManager(OrderFactory orderFactory, TriagingEngine triagingEngine, OrderAccess orderAccess, CommandLog commandLog, NotificationChain notificationChain) {
        this.orderFactory = orderFactory;
        this.triagingEngine = triagingEngine;
        this.orderAccess = orderAccess;
        this.commandLog = commandLog;
        this.notificationChain = notificationChain;
    }

    public void createOrder(String[] order) {
        String actor = order[2];
        int orderId = orderAccess.getNextOrderID();
        Order newOrder = orderFactory.create(order, orderId);
        Command create = commandLog.getSubmitCommand(newOrder.getOrderID());
        create.execute(actor);
        lastCommand = create;
    }

    public void cancelOrder(int orderId, String clinician) {
        Command cancel = commandLog.getCancelCommand(orderId);
        cancel.execute(clinician);
        lastCommand = cancel;
    }

    public void completeOrder(int orderId, String staffMember) {
        Command complete = commandLog.getCompleteCommand(orderId);
        complete.execute(staffMember);
        lastCommand = complete;
    }

    public void claimOrder(int orderId, String staffMember) {
        Command claim = commandLog.getClaimCommand(orderId);
        claim.execute(staffMember);
        lastCommand = claim;
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

    public void undoCommand(String user){
        if(lastCommand != null){
            lastCommand.undo(user);
            lastCommand = null;
        }
    }

    // notifications : [ user , String array of all notification types to be included]
    public void setNotifications(String[] notifications){
        String user = notifications[0];
        notificationChain.setNotifications(user, Arrays.copyOfRange(notifications, 1, notifications.length));
    }

    public ArrayList<String> getNotifications(){
        return notificationChain.getActiveNotificationsList();
    }

    // user : [ department, employeeId ]
    public void updateUser(String[] user){
        notificationChain.updateUser(user[0] + user[1]);
        triagingEngine.departmentChange(user[0]);
    }

    // CommandInfo needs [orderId, commandType, and actor]
    public void replayCommand(String[] commandInfo){
        int commandId = Integer.parseInt(commandInfo[0]);
        String commandType = commandInfo[1];
        switch (commandType.toLowerCase()){
            case "claim":
                commandLog.getClaimCommand(commandId).execute(commandInfo[2]);
                break;
            case "complete":
                commandLog.getCompleteCommand(commandId).execute(commandInfo[2]);
                break;
            case "cancel":
                commandLog.getCancelCommand(commandId).execute(commandInfo[2]);
                break;
            case "submit":
                commandLog.getSubmitCommand(commandId).execute(commandInfo[2]);
                break;
        }
    }
}
