package com.project1.project1.Notification;

import com.project1.project1.User.Portfolio;

import java.util.Objects;

public class EmailNotify extends Notification {

    public EmailNotify(NotificationService service) {
        super(service);
    }

    public void sendNotification(String m) {
        super.sendNotification(m);

        String[] message = m.split(",");
        if(Objects.equals(message[0], "balance")){
            System.out.println("Email Message: Dear user, \n Your order couldn't be filled as you have insufficient funds. Balance: " + message[3] + " Order Cost: " + (Double.parseDouble(message[2]) * Double.parseDouble(message[1])));
        }else if(Objects.equals(message[0], "trade")){
            System.out.println(String.format("Email Message: Dear user, \n %s order of type %s: %s At %.2f for %.2f shares.", message[1], message[3], message[2], Double.parseDouble(message[4]), Double.parseDouble(message[5])));
        } else if (Objects.equals(message[0], "objectError")) {
            System.out.println(String.format("Email Message: Dear user, \n The object you tried to trade isn't available. Cannot create order with name: %s\n", message[1]));
        }else if (Objects.equals(message[0], "orderError")) {
            System.out.println(String.format("Email Message: Dear user, \n Your order couldn't be placed as there is no order of type: %s\n", message[1]));
        }else if (Objects.equals(message[0], "holding")) {
            System.out.println(String.format("Email Message: Dear user, \nOrder failed as cannot sell holding of type: %s\n", message[1]));
        }
    }
}
