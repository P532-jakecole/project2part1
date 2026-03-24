package com.project1.project1.Trading;

import static org.mockito.Mockito.*;

import com.project1.project1.Feed.Feed;
import com.project1.project1.Feed.FeedObject;
import com.project1.project1.Notification.BaseNotification;
import com.project1.project1.Notification.ConsoleNotify;
import com.project1.project1.Updating.FeedService;
import com.project1.project1.Notification.NotificationService;
import com.project1.project1.Pricing.Market;
import com.project1.project1.Repository.PendingOrders;
import com.project1.project1.Repository.TradeHistory;
import com.project1.project1.User.Holding;
import com.project1.project1.User.Portfolio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LimitOrderTest {

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

//    @Mock
//    NotificationService notificationService;

    @Mock
    TradeHistory tradeHistory;

    OrderFactory orderFactory;
    NotificationService notificationService;

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
    void limitBuyExecutesWhenPriceBelowThreshold() {

        // Arrange
        when(portfolio.getCashBalance()).thenReturn(1000.0);
        LimitOrder order = new LimitOrder("AAPL", 150.0, 1, "buy", market,
                pendingOrders, portfolio, notificationService, tradeHistory);

        FeedObject stock = mock(FeedObject.class);
        when(stock.getPrice()).thenReturn(140.0);

        // Act
        order.update(stock.getPrice());

        // Assert
        verify(portfolio).reduceCashBalance(140.0);

        verify(portfolio).addHolding(argThat(h ->
                h.getName().equals("AAPL") &&
                        h.getQuantity() == 1 &&
                        h.getPrice() == 140.0
        ));
    }

        @Test
        void limitBuyDoesNotExecuteWhenPriceAboveThreshold() {

            // Arrange
            LimitOrder order = new LimitOrder("AAPL", 150.0, 1, "buy", market,
                    pendingOrders, portfolio, notificationService, tradeHistory);

            FeedObject stock = mock(FeedObject.class);
            when(stock.getPrice()).thenReturn(160.0);

            // Act
            order.update(stock.getPrice());

            // Assert
            verify(portfolio, never()).reduceCashBalance(anyDouble());
            verify(portfolio, never()).addHolding(any());
        }

    @Test
    void limitSellExecutesWhenPriceAboveThreshold() {

        // Arrange
        Holding hold = new Holding("AAPL", 1, 200);
        when(portfolio.getHolding("AAPL")).thenReturn(hold);

        LimitOrder order = new LimitOrder("AAPL", 150.0, 1, "sell", market,
                pendingOrders, portfolio, notificationService, tradeHistory);

        FeedObject stock = mock(FeedObject.class);
        when(stock.getPrice()).thenReturn(160.0);

        // Act
        order.update(stock.getPrice());

        // Assert
        verify(portfolio).addCashBalance(160.0);

        verify(portfolio).removeHolding(argThat(h ->
                h.getName().equals("AAPL") &&
                        h.getQuantity() == 1 &&
                        h.getPrice() == 160.0
        ));
    }

    @Test
    void limitSellDoesNotExecuteWhenPriceBelowThreshold() {

        // Arrange
        LimitOrder order = new LimitOrder("AAPL", 150.0, 1, "sell", market,
                pendingOrders, portfolio, notificationService, tradeHistory);

        FeedObject stock = mock(FeedObject.class);
        when(stock.getPrice()).thenReturn(140.0);

        // Act
        order.update(stock.getPrice());

        // Assert
        verify(portfolio, never()).addCashBalance(anyDouble());
        verify(portfolio, never()).removeHolding(any());
    }

}
