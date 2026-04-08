package com.project2.Decorator;

import com.project2.Factory.Order;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class NotificationChain implements NotificationService {
    private final HashMap<String, NotificationService> userNotifications = new HashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    private NotificationService activeNotifications;

    public NotificationChain(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void setActiveNotifications(NotificationService activeNotifications) {
        this.activeNotifications = activeNotifications;
    }

    public NotificationService getActiveNotifications() {
        return activeNotifications;
    }

    public ArrayList<String> getActiveNotificationsList() {
        ArrayList<String> chain = new ArrayList<>();
        NotificationService current = getActiveNotifications();

        while (current != null) {
            chain.add(current.getClass().getSimpleName());
            try {
                current = (NotificationService) current.getClass().getField("notificationService").get(current);
            } catch (NoSuchFieldException e) {
                return chain;
            } catch (IllegalAccessException e) {
                break;
            }
        }

        return chain;
    }

    public void updateUser(String user){
        if(userNotifications.containsKey(user)){
            activeNotifications = userNotifications.get(user);
        }else{
            userNotifications.put(user, new ConsoleNotification(new BaseNotification(), messagingTemplate));
            activeNotifications = userNotifications.get(user);
        }
    }

    public void setNotifications(String user, String[] notifications) {
        NotificationService chain = new BaseNotification();

        for(String notification : notifications) {
            switch (notification.toLowerCase()){
                case "console":
                    chain = new ConsoleNotification(chain, messagingTemplate);
                    break;
                case "email":
                    chain = new EmailNotification(chain, messagingTemplate);
                    break;
                case "alert":
                    chain = new AlertNotification(chain, messagingTemplate);
                    break;
            }
        }
        userNotifications.put(user, chain);
        activeNotifications = chain;
    }

    @Override
    public void notify(Order order, String event) {
        activeNotifications.notify(order, event);
    }

    @Override
    public void update(Order order, String event) {
        notify(order, event);
    }
}
