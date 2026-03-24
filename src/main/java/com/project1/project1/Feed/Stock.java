package com.project1.project1.Feed;


import com.project1.project1.Pricing.Observer;
import com.project1.project1.Updating.FeedService;
import com.project1.project1.User.Portfolio;

import java.util.ArrayList;
import java.util.HashMap;

public class Stock implements FeedObject {

    private String stockName;
    private double stockPrice;
    private Portfolio portfolio;

    private HashMap<Integer, ArrayList<Observer>> observers = new HashMap<>();

    private FeedService feedService;

    public Stock(String stockName, double stockPrice, FeedService feedService, Portfolio portfolio) {
        this.stockName = stockName;
        this.stockPrice = stockPrice;
        this.feedService = feedService;
        this.portfolio = portfolio;

        for(int i = 1; i < 4; i++){
            observers.put(i, new ArrayList<>());
        }
    }

    public String getName() {
        return stockName;
    }

    public double getPrice(){
        return stockPrice;
    }

    public void update(double price){
        this.stockPrice = price;
        notifyObserver(price);

        feedService.sendUpdate(this);
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.get(portfolio.getUserId()).add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.get(portfolio.getUserId()).remove(observer);
    }

    @Override
    public void notifyObserver() {}

    public void notifyObserver(double price) {
        if(!observers.get(portfolio.getUserId()).isEmpty()){
            for (Observer observer : observers.get(portfolio.getUserId())) {
                observer.update(price);
            }
        }
    }
}
