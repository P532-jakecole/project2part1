package com.project1.project1.Pricing;

import com.project1.project1.Feed.FeedObject;
import com.project1.project1.Updating.FeedService;
import com.project1.project1.Updating.OrderService;
import com.project1.project1.Trading.Order;
import com.project1.project1.User.Portfolio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class Market implements Subject {
    @Autowired
    private FeedService feedService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private Portfolio portfolio;

//    private HashMap<Observer, PricingModel> observers;
//    private HashMap<Observer, Integer> observers;
    private ArrayList<Observer> observers;

    private Market() {
        //observers = new HashMap<>();
        observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(Observer observer) {
        //observers.put(observer, portfolio.getUserId());
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
        if (observer instanceof FeedObject object) {
            feedService.sendUpdate(object);
        }
        if(observer instanceof Order order){
            orderService.sendUpdate(order);
        }
    }

    @Override
    public void notifyObserver() {
//        HashMap<Observer, Integer> copy = new HashMap<>(observers);
//
//        for (Map.Entry<Observer, Integer> entry : copy.entrySet()) {
//
//            Observer observer = entry.getKey();
//            PricingModel pricingModel = portfolio.getPricingModel(entry.getValue());
//            System.out.println("Value of pricingModel in Market: " + pricingModel);
//            if(pricingModel == null) continue;
//
//            double newPrice = pricingModel.updatePrice(observer);
//
//            observer.update(newPrice);
//
//            if (observer instanceof FeedObject object) {
//                feedService.sendUpdate(object);
//            }
//        }

        ArrayList<Observer> copy = new ArrayList<>(observers);

        for (Observer entry : copy) {

            PricingModel pricingModel = portfolio.getPricingModel();
            if(pricingModel == null) continue;

            double newPrice = pricingModel.updatePrice(entry);

            entry.update(newPrice);

            if (entry instanceof FeedObject object) {
                feedService.sendUpdate(object);
            }
        }
    }
}
