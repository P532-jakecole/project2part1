package com.project1.project1.Controllers;

import com.project1.project1.Notification.NotificationService;
import com.project1.project1.Repository.OrderRepository;
import com.project1.project1.Repository.PendingOrders;
import com.project1.project1.Trading.Order;
import com.project1.project1.User.Holding;
import com.project1.project1.User.Portfolio;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(
        origins = "https://p532-jakecole.github.io",
        allowedHeaders = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
@RequestMapping("/home")
public class OrderController {

    private final PendingOrders pendingOrders;
    private OrderRepository orderRepository;
    private Portfolio portfolio;

    public OrderController(OrderRepository orderRepository, Portfolio portfolio, PendingOrders pendingOrders) {
        this.orderRepository = orderRepository;
        this.portfolio = portfolio;
        this.pendingOrders = pendingOrders;
    }

    @PostMapping
    public void add(@RequestBody String[] order) {
        try{
            orderRepository.save(order);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping()
    public List<String> findAll() {
        try {
            return orderRepository.findAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/holding")
    public List<Holding> findAllHoldings() {
        return portfolio.getAllHoldings();
    }

    @GetMapping("/pending")
    public List<String> findAllPending() {
        return pendingOrders.getAll();
    }

    @GetMapping("/info")
    public String userInformation() {
        return portfolio.userInformation();
    }

    @PostMapping("/info")
    public void setUser(@RequestBody Integer id) {
        System.out.println("In Controller");
        portfolio.setUser(id);
    }

    @PostMapping("/info/notification/add")
    public void addNotification(@RequestBody String notification) {
        portfolio.addNotification(notification);
    }

    @PostMapping("/info/notification/sub")
    public void subNotification(@RequestBody String notification) {
        portfolio.subNotification(notification);
    }

    @GetMapping("/info/notify")
    public List<String> getNotifications() {
        return portfolio.getNotificationList();
    }

    @PostMapping("/price")
    public void setPriceAlgo(@RequestBody String algo) {
        portfolio.setPricingModel(algo);
    }

    @GetMapping("/price")
    public String getPriceAlgo() {
        return portfolio.getCurrentPricingModel();
    }

}
