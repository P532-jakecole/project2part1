package com.project1.project1.Trading;

import com.project1.project1.Feed.Feed;
import com.project1.project1.Feed.FeedObject;
import com.project1.project1.Notification.ConsoleNotify;
import com.project1.project1.Notification.NotificationService;
import com.project1.project1.Pricing.Market;
import com.project1.project1.Pricing.Observer;
import com.project1.project1.Repository.PendingOrders;
import com.project1.project1.Repository.TradeHistory;
import com.project1.project1.User.Portfolio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderFactory {

    private final Feed feed;
    private final Market market;
    private final PendingOrders pendingOrders;
    private final Portfolio portfolio;
    private NotificationService notificationService;
    private final TradeHistory tradeHistory;

    public OrderFactory(
            Feed feed,
            Market market,
            PendingOrders pendingOrders,
            Portfolio portfolio,
//            NotificationService notificationService,
            TradeHistory tradeHistory) {

        this.feed = feed;
        this.market = market;
        this.pendingOrders = pendingOrders;
        this.portfolio = portfolio;
        this.tradeHistory = tradeHistory;
    }

    public Order createOrder(String type, String action, String name, double price, double quantity) {
        this.notificationService = this.portfolio.getNotifications();
        if(feed.getObject(name) == null){
            notificationService.sendNotification(String.format("objectError,%s", name));
        }
        if(type.equals("Market")){
            double stockPrice = feed.getObject(name).getPrice();
            if(action.equals("sell")){
                if(portfolio.getHolding(name) == null || portfolio.getHolding(name).getQuantity() < quantity){
                    notificationService.sendNotification(String.format("holding,%s", name));
                    return null;
                }
            }
            if(action.equals("buy") && portfolio.getCashBalance() < (stockPrice*quantity)){
                notificationService.sendNotification(String.format("balance,%.2f,%.2f,%.2f", price, quantity, portfolio.getCashBalance()));
                return null;
            }
            Order marketOrder = new MarketOrder(name, stockPrice, quantity, action, portfolio);
            marketOrder.executeOrder();
            notificationService.sendNotification(String.format("trade,%s", marketOrder.getOrder()));
            tradeHistory.save(marketOrder);
            return marketOrder;
        }else if(type.equals("Limit")){
            Observer limitOrder = new LimitOrder(name, price, quantity, action, market, pendingOrders, portfolio, notificationService, tradeHistory);
            FeedObject obj = feed.getObject(name);
            obj.registerObserver(limitOrder);
            pendingOrders.save((Order) limitOrder);
            return (Order) limitOrder;
        }
        notificationService.sendNotification(String.format("orderError,%s", type));
        return null;
    }
}
