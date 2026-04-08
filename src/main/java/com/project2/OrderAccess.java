package com.project2;

import com.project2.Decorator.NotificationService;
import com.project2.Factory.Order;
import com.project2.Factory.OrderFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class OrderAccess {
    private final NotificationService notificationService;
    HashMap<String, ArrayList<Order>> progressOrders = new HashMap<>();
    private List<String> departments = new ArrayList<>();

    public OrderAccess(NotificationService notificationService) {
        this.notificationService = notificationService;
        departments.add("lab");
        departments.add("imaging");
        departments.add("medication");
        for(String department : departments) {
            for(int i = 1; i <= 4; i++){
                progressOrders.put(department + i, new ArrayList<>());
            }
        }
    }

    int nextOrderID = 1;
    ArrayList<Order> pendingOrders = new ArrayList<>();

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

//    public void cancelOrder(int id) {
//        Order pendingOrder = findOrderById(id);
//        if(pendingOrder == null || !pendingOrder.getStatus().equalsIgnoreCase("pending")) {
//           notificationService.notify(pendingOrder, "Cancel error");
//        }else{
//            pendingOrder.setStatus("CANCELLED");
//            pendingOrders.remove(pendingOrder);
//        }
//    }
//
//    public void completeOrder(int orderId, String staff) {
//        ArrayList<Order> orders = progressOrders.get(staff);
//        if(!orders.isEmpty()) {
//            Order order = orders.get(0);
//            order.setStatus("COMPLETE");
//            progressOrders.get(staff).remove(order);
//        }
//    }
//
//    public void claimOrder(int orderId, String member) {
//        Order pendingOrder = findOrderById(orderId);
//        if(pendingOrder != null) {
//            pendingOrder.setStatus("IN_PROGRESS");
//            if(!progressOrders.get(member).isEmpty()) {
//                ArrayList<Order> orders = progressOrders.get(member);
//                orders.add(pendingOrder);
//                progressOrders.put(member, orders);
//            }else{
//                ArrayList<Order> newArray = new ArrayList<>();
//                newArray.add(pendingOrder);
//                progressOrders.put(member, newArray);
//            }
//        }
//    }

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

    public int getNextOrderID(){
        return nextOrderID;
    }

    public void incrimentOrderId(){
        nextOrderID++;
    }

    public void setPendingOrders(ArrayList<Order> pendingOrders){
        this.pendingOrders = pendingOrders;
    }
}
