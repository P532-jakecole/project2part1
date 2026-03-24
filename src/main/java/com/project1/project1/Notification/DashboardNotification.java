package com.project1.project1.Notification;

import com.project1.project1.Updating.OrderService;
import com.project1.project1.User.Portfolio;

import java.util.Objects;

public class DashboardNotification extends Notification {
    private OrderService orderService;
    private Integer userId;
    private Integer badgeCount;

    public DashboardNotification(OrderService orderService, Integer userId, NotificationService service) {
        super(service);
        this.orderService = orderService;
        this.userId = userId;
        badgeCount = 0;
    }

    public void sendNotification(String m) {
        super.sendNotification(m);

        String UIMessage = "";
        String[] message = m.split(",");
        if(Objects.equals(message[0], "balance")){
            UIMessage = "Error: Order couldn't be filled as you have insufficient funds. Balance: " + message[3] + " Order Cost: " + (Double.parseDouble(message[2]) * Double.parseDouble(message[1]));
        }else if(Objects.equals(message[0], "trade")){
            badgeCount += 1;
            orderService.sendNotification(userId + "," + badgeCount);
            UIMessage = String.format("%s order of type %s: %s At %.2f for %.2f shares.", message[1], message[3], message[2], Double.parseDouble(message[4]), Double.parseDouble(message[5]));
        } else if (Objects.equals(message[0], "objectError")) {
            UIMessage = String.format("Error: Wrong Name. Cannot create order with name: %s\n", message[1]);
        }else if (Objects.equals(message[0], "orderError")) {
            UIMessage = String.format("Error: There is no order of type: %s\n", message[1]);
        }else if (Objects.equals(message[0], "holding")) {
            UIMessage = String.format("Error: Cannot sell holding of type: %s\n", message[1]);
        }


    }
}
