package com.project1.project1.Trading;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.project1.project1.Feed.Feed;
import com.project1.project1.Feed.FeedObject;
import com.project1.project1.Notification.BaseNotification;
import com.project1.project1.Notification.ConsoleNotify;
import com.project1.project1.Updating.FeedService;
import com.project1.project1.Feed.Stock;
import com.project1.project1.Notification.NotificationService;
import com.project1.project1.Pricing.Market;
import com.project1.project1.Repository.PendingOrders;
import com.project1.project1.Repository.TradeHistory;
import com.project1.project1.User.Portfolio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MarketOrderTest {

    @Mock
    FeedService feedService;

    @Mock
    Feed feed;

    @Mock
    Market market;

    @Mock
    PendingOrders pendingOrders;

    @Mock
    Portfolio portfolio;

    NotificationService notificationService;

    @Mock
    TradeHistory tradeHistory;

    OrderFactory orderFactory;

    @BeforeEach
    void setup() {
        notificationService = new ConsoleNotify(new BaseNotification());

        orderFactory = new OrderFactory(
                feed,
                market,
                pendingOrders,
                portfolio,
//                notificationService,
                tradeHistory
        );
    }


    @Test
    void marketBuyUpdatesPortfolioCorrectly() {

        // Arrange
        MarketOrder order = new MarketOrder("AAPL", 100.0, 2, "buy", portfolio);

        // Act
        order.executeOrder();

        // Assert
        verify(portfolio).reduceCashBalance(200.0);

        verify(portfolio).addHolding(argThat(holding ->
                holding.getName().equals("AAPL") &&
                        holding.getQuantity() == 2 &&
                        holding.getPrice() == 100.0
        ));

    }

    @Test
    void marketSellUpdatesPortfolioCorrectly() {
        // Arrange
        MarketOrder order = new MarketOrder("AAPL", 100.0, 1, "sell", portfolio);

        // Act
        order.executeOrder();


        // Assert
        verify(portfolio).addCashBalance(100.0);
        verify(portfolio).removeHolding(argThat(hold ->
                hold.getName().equals("AAPL") &&
                        hold.getQuantity() == 1 &&
                        hold.getPrice() == 100.0
        ));

    }

    @Test
    void marketBuyFailsWhenInsufficientFunds() {

        // Arrange
        FeedObject stock = new Stock("AAPL", 150.0, feedService, portfolio);

        when(feed.getObject("AAPL")).thenReturn(stock);
        when(portfolio.getCashBalance()).thenReturn(100.0);
        when(portfolio.getNotifications()).thenReturn(notificationService);

        // Act
        Order order = orderFactory.createOrder("Market", "buy", "AAPL", 150.0, 1);

        // Assert
        assertNull(order);
        verify(portfolio, never()).reduceCashBalance(anyDouble());
    }

    @Test
    void marketSellFailsWhenHoldingDoesNotExist() {

        // Arrange
        FeedObject stock = new Stock("AAPL", 150.0, feedService, portfolio);

        when(feed.getObject("AAPL")).thenReturn(stock);
        when(portfolio.getHolding("AAPL")).thenReturn(null);
        when(portfolio.getNotifications()).thenReturn(notificationService);

        // Act
        Order order = orderFactory.createOrder("Market", "sell", "AAPL", 150.0, 1);

        // Assert
        assertNull(order);
        verify(portfolio, never()).addCashBalance(anyDouble());
    }

}
