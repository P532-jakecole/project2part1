package com.project1.project1.Pricing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MeanReversion implements PricingModel{
    private Random rand;
    private Integer windowSize;
    private double strength;
    private List<Double> prices = new ArrayList<Double>();

    public MeanReversion(Random rand) {
        this.rand = rand;
        this.windowSize = 5;
        this.strength = 0.1;
    }

    @Override
    public double updatePrice(Observer observer) {
        double price = observer.getPrice();

        prices.add(price);
        if(prices.size() > windowSize){
            prices.remove(0);
        }

        double sum = 0.0;
        int count = prices.size();
        for (double oldPrice : prices) {
            sum += oldPrice;
        }
        double mean = (count == 0) ? price : sum / count;
        double pull = strength * (mean - price);
        double random = rand.nextDouble() * 2 - 1;

        return price + pull + random;
    }
}
