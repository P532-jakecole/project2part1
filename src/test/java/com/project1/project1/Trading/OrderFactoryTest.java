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
class OrderFactoryTest {

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
    void marketBuySuccess() {

        // Arrange
        FeedObject stock = new Stock("AAPL", 150.0, feedService, portfolio);

        when(feed.getObject("AAPL")).thenReturn(stock);
        when(portfolio.getCashBalance()).thenReturn(200.0);
        when(portfolio.getNotifications()).thenReturn(notificationService);

        // Act
        Order order = orderFactory.createOrder("Market", "buy", "AAPL", 150.0, 1);

        // Assert
        if (order == null) {
            fail();
        }
        verify(portfolio).addHolding(argThat(holding ->
                holding.getName().equals("AAPL") &&
                        holding.getQuantity() == 1 &&
                        holding.getPrice() == 150.0
        ));
    }

    @Test
    void limitBuySuccess() {

        // Arrange
        FeedObject stock = new Stock("AAPL", 180.0, feedService, portfolio);
        when(feed.getObject("AAPL")).thenReturn(stock);
        when(portfolio.getNotifications()).thenReturn(notificationService);
        when(portfolio.getUserId()).thenReturn(1);

        // Act
        Order order = orderFactory.createOrder("Limit", "buy", "AAPL", 150.0, 1);

        // Assert
        if (order == null) {
            fail();
        }

        verify(pendingOrders).save(argThat(order1 ->
                order1.getName().equals("AAPL") &&
                        order1.getQuantity() == 1 &&
                        order1.getPrice() == 150.0
        ));
    }

    @Test
    void orderNameFail() {

        // Arrange
        FeedObject stock = new Stock("AAPL", 180.0, feedService, portfolio);
        when(feed.getObject("AAPL")).thenReturn(stock);
        when(portfolio.getNotifications()).thenReturn(notificationService);

        // Act
        Order order = orderFactory.createOrder("MOCK", "buy", "AAPL", 150.0, 1);

        // Assert
        assertNull(order);
        verify(portfolio, never()).reduceCashBalance(anyDouble());
    }

}