package com.project2;

import com.project2.Factory.Order;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(
        origins = "https://p532-jakecole.github.io",
        allowedHeaders = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
@RequestMapping("/home")
public class OrderController {

    private OrderManager orderManager;

    public OrderController(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    @PostMapping
    public void create(@RequestBody String[] order) {
        try{
            orderManager.createOrder(order);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/cancel")
    public void cancelOrder(@RequestBody String[] cancel) {
        try{
            orderManager.cancelOrder(Integer.parseInt(cancel[0]), cancel[1]);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/claim")
    public void claimOrder(@RequestBody String[] claim) {
        try{
            System.out.println(claim);
            orderManager.claimOrder(Integer.parseInt(claim[0]), claim[1]);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/complete")
    public void completeOrder(@RequestBody String[] complete) {
        try{
            orderManager.completeOrder(Integer.parseInt(complete[0]), complete[1]);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/triage")
    public void update(@RequestBody String triage) {
        try{
            orderManager.updateTriage(triage);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping()
    public List<Order> findAll() {
        try {
            return orderManager.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/order")
    public Order next() {
        try {
            return orderManager.getNextOrder();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/log")
    public ArrayList<String[]> getCommandLog() {
        try {
            return orderManager.getCommandLog();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
