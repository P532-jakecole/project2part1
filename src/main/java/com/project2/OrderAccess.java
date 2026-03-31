package com.project2;

import com.project2.Decorator.NotificationService;
import com.project2.Factory.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Service
public class OrderAccess {
    private final NotificationService notificationService;

    public OrderAccess(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    ArrayList<Order> pendingOrders = new ArrayList<>();
    HashMap<String, Order> progressOrders = new HashMap<>();

    public void saveOrder(Order order) {
        pendingOrders.add(order);
    }

    public void saveOrder(int position, Order order) {
        pendingOrders.add(position, order);
    }

    public Order findOrderById(int id){
        for (Order order : pendingOrders) {
            if (order.getOrderID() == id) {
                return order;
            }
        }
        return null;
    }

    public void removeOrder(Order order) {
        pendingOrders.remove(order);
    }

    public void cancelOrder(int id) {
        Order pendingOrder = findOrderById(id);
        if(pendingOrder == null || !pendingOrder.getStatus().equalsIgnoreCase("pending")) {
           notificationService.notify(pendingOrder, "Cancel error");
        }else{
            pendingOrder.setStatus("CANCELLED");
        }
    }

    public void completeOrder(int orderId, String staff) {
        Order order = progressOrders.get(staff);
        if(order != null) {
            order.setStatus("COMPLETE");
            progressOrders.remove(staff);
        }
    }

    public void claimOrder(int orderId, String member) {
        Order pendingOrder = findOrderById(orderId);
        if(pendingOrder != null) {
            pendingOrder.setStatus("IN_PROGRESS");
            progressOrders.put(member, pendingOrder);
        }
    }

    public ArrayList<Order> listPendingOrders(){
        return pendingOrders;
    }

    public Order getNextOrder(){
        for (Order order : pendingOrders) {
            if (Objects.equals(order.getStatus(), "PENDING")){
                return order;
            }
        }
        return null;
    }

    public Order getNextOrder(String user){
        for (Order order : pendingOrders) {
            if (Objects.equals(order.getType(), user) && Objects.equals(order.getStatus(), "PENDING")) {
                return order;
            }
        }
        return null;
    }
}
