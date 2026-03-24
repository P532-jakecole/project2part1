package com.project1.project1.User;

import com.project1.project1.Notification.*;
import com.project1.project1.Pricing.*;
import com.project1.project1.Updating.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

@Service
public class Portfolio {
    private OrderService orderService;


    //private static final Portfolio account = new Portfolio();
    private Integer userId;
    private PricingModel pricingModel = new RandomWalk(new Random());
    private double cashBalance = 10000;
    private final String NEW_LINE = System.lineSeparator();

    private BaseNotification baseNotification;

    private HashMap<Integer, Double> userBalance = new HashMap<>();
    private HashMap<Integer, PricingModel> userPricingModel = new HashMap<>();
    private HashMap<Integer, NotificationService> userNotifications = new HashMap<>();

    @Autowired
    public Portfolio(OrderService orderService) {
        this.orderService = orderService;
        baseNotification = new BaseNotification();
        userId = 1;

        for(int i = 1; i < 4; i++){
            userBalance.put(i, 10000.00);
            userPricingModel.put(i, pricingModel);
            userNotifications.put(i, new ConsoleNotify(baseNotification));
        }
    }

    private String FILE_NAME = String.format("data/%d/Holdings.txt", userId);
    private double portfolioBalance = updatePortfolioBalance();

    private void appendToFile(Path path, String content)
            throws IOException {
        content += NEW_LINE;
        Files.write(path,
                content.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    public void setCashBalance(double balance) {
        this.cashBalance = balance;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public void reduceCashBalance(double amount) {
        cashBalance -= amount;
    }

    public void addCashBalance(double amount) {
        cashBalance += amount;
    }

    public void setUser(Integer userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    private void updateDatabase(){
        FILE_NAME = String.format("data/%d/Holdings.txt", userId);
    }

    public void setPricingModel(String pricingModel) {
         //pm = null;
        System.out.println(pricingModel.toLowerCase());
        System.out.println(pricingModel.equalsIgnoreCase("trendfollowing"));
        System.out.println(pricingModel.equalsIgnoreCase("\"trendfollowing\""));
        PricingModel pm = switch (pricingModel.toLowerCase().replaceAll("\"", "")) {
            case "meanreversion" -> new MeanReversion(new Random());
            case "trendfollowing" -> new TrendFollowing(new Random());
            case "randomwalk" -> new RandomWalk(new Random());
            default -> null;
        };
        System.out.printf("Set Pricing Model %s\n", pm);
        userPricingModel.put(userId, pm);

    }

    public PricingModel getPricingModel() {
        return userPricingModel.get(userId);
    }

    public String getCurrentPricingModel() {
        PricingModel pm = userPricingModel.get(userId);
        return pm.getClass().getSimpleName();
    }

    public void addNotification(String notification) {
        NotificationService current = userNotifications.get(userId);

        if (current == null) {
            current = new BaseNotification();
        }

        switch (notification.toLowerCase()) {

            case "console":
                current = new ConsoleNotify(current);
                break;

            case "sms":
                current = new SMSNotify(current);
                break;

            case "email":
                current = new EmailNotify(current);
                break;

            case "dashboard":
                current = new DashboardNotification(orderService, userId, current);
                break;
        }

        userNotifications.put(userId, current);
    }

    public void subNotification(String notification) {
        NotificationService current = userNotifications.get(userId);

        if (current == null) {
            return;
        }

        Class<?> targetNotification = null;

        switch(notification.toLowerCase()) {

            case "console":
                targetNotification = ConsoleNotify.class;
                break;

            case "sms":
                targetNotification = SMSNotify.class;
                break;

            case "email":
                targetNotification = EmailNotify.class;
                break;

            case "dashboard":
                targetNotification = DashboardNotification.class;
                break;
        }

        if (targetNotification == null) return;

        if (targetNotification.isInstance(current) && current instanceof Notification n) {
            userNotifications.put(userId, n.notificationService);
            return;
        }

        NotificationService prev = current;

        while (prev instanceof Notification decorator) {

            NotificationService next = decorator.notificationService;

            if (targetNotification.isInstance(next)) {
                decorator.notificationService = ((Notification) next).notificationService;
                break;
            }

            prev = next;
        }

        userNotifications.put(userId, current);
    }

    public NotificationService getNotifications(){
        return userNotifications.get(userId);
    }

    public List<String> getNotificationList(){
        NotificationService current = getNotifications();
        List<String> notifications = new ArrayList<>();

        while (current instanceof Notification decorator) {

            if (decorator instanceof ConsoleNotify) {
                notifications.add("console");
            }
            else if (decorator instanceof SMSNotify) {
                notifications.add("sms");
            }
            else if (decorator instanceof EmailNotify) {
                notifications.add("email");
            }
            else if (decorator instanceof DashboardNotification) {
                notifications.add("dashboard");
            }

            current = decorator.notificationService;
        }

        return notifications;
    }

    public List<Holding> getAllHoldings() {
        ArrayList<Holding> holding = new ArrayList<>();
        updateDatabase();

        try{
            Path path = Paths.get(FILE_NAME);
            if(Files.exists(path)){
                List<String> orderList = Files.readAllLines(path, StandardCharsets.UTF_8);
                for(String line : orderList){
                    String[] values = line.split(",");
                    Holding h = new Holding(values[0], Double.parseDouble(values[1]), Double.parseDouble(values[2]));
                    holding.add(h);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return holding;
    }


    public Holding getHolding(String name) {
        updateDatabase();
        Path path = Paths.get(FILE_NAME);
        List<Holding> holdings = getAllHoldings();

        for(Holding h : holdings){
            if(h.getName().equals(name)){
                return h;
            }
        }
        return null;
    }

    public void addHolding(Holding holding) {
        try {
            updateDatabase();
            Path path = Paths.get(FILE_NAME);
            List<Holding> holdings = getAllHoldings();
            boolean found = false;

            for(Holding h : holdings){
                if(h.getName().equals(holding.getName())){
                    h.buyUpdate(holding);
                    found = true;
                    orderService.addHolding(h);
                    break;
                }
            }


            if(!found){
                appendToFile(path, holding.toString());
                updatePortfolioBalance();
                orderService.updateBalance(userInformation());
                orderService.addHolding(holding);
                return;
            }
            Files.write(path, new byte[0]);

            for(Holding h : holdings){
                appendToFile(path, h.toString());
            }

            updatePortfolioBalance();
            orderService.updateBalance(userInformation());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeHolding(Holding holding) {
        try {
            updateDatabase();

            Path path = Paths.get(FILE_NAME);
            List<Holding> holdings = getAllHoldings();
            Iterator<Holding> iterator = holdings.iterator();

            while(iterator.hasNext()){
                Holding h = iterator.next();

                if(h.getName().equals(holding.getName())){
                    System.out.println("In Remove Holding");
                    System.out.println(h.getValue());
                    h.sellUpdate(holding);
                    System.out.println(h.getValue());

                    orderService.removeHolding(h);
                    if(h.getQuantity() == 0){
                        iterator.remove();
                    }
                    break;
                }
            }


            List<String> updated = new ArrayList<>();
            for(Holding h : holdings){
                updated.add(h.toString());
            }
            Files.write(path, updated);

            portfolioBalance = updatePortfolioBalance();
            orderService.updateBalance(userInformation());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double updatePortfolioBalance(){
        double sumHoldings = 0;
        for(Holding h : getAllHoldings()){
            sumHoldings += h.getValue();
        }
        return sumHoldings + cashBalance;
    }

    public String userInformation(){
        return String.format("%.2f,%.2f", cashBalance, portfolioBalance);
    }
}
