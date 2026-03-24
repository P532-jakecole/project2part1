package com.project1.project1.Pricing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrendFollowing implements PricingModel{
    private Random rand;
    private Integer windowSize;
    private double strength;
    private List<Double> prices = new ArrayList<Double>();

    public TrendFollowing(Random rand) {
        this.rand = rand;
        this.windowSize = 5;
        this.strength = 0.1;
    }

    @Override
    public double updatePrice(Observer observer) {

        double price = observer.getPrice();

        double sum = 0.0;
        int count = prices.size();
        double prev = -1;
        for (double oldPrice : prices) {
            if (prev > 0){
                sum += (oldPrice - prev);
            }
            prev = oldPrice;
        }

        double noise = rand.nextDouble() * 2 - 1;
        double mom = (count <= 1) ? 0.0 : sum / (count-1);
        double trend = strength * mom;

        prices.add(price);
        if(prices.size() > windowSize){
            prices.remove(0);
        }

        return price + trend + noise;
    }
}