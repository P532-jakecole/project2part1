package com.project1.project1;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import com.project1.project1.Feed.Feed;
import com.project1.project1.Feed.FeedObject;
import com.project1.project1.Feed.Stock;
import com.project1.project1.Notification.*;
import com.project1.project1.Pricing.Market;
import com.project1.project1.Repository.PendingOrders;
import com.project1.project1.Repository.TradeHistory;
import com.project1.project1.Trading.Order;
import com.project1.project1.Trading.OrderFactory;
import com.project1.project1.Updating.FeedService;
import com.project1.project1.Updating.OrderService;
import com.project1.project1.User.Portfolio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@ExtendWith(MockitoExtension.class)
class NotificationTest {

    @Mock
    FeedService feedService;

    @Mock
    OrderService orderService;

    @Mock
    Feed feed;

    @Mock
    Market market;

    @Mock
    PendingOrders pendingOrders;

    @Mock
    Portfolio portfolio;

    @Mock
    TradeHistory tradeHistory;

    ConsoleNotify consoleNotify;
    BaseNotification notification;

    OrderFactory orderFactory;

    @BeforeEach
    void setup() {
        notification = new BaseNotification();
        consoleNotify = new ConsoleNotify(notification);

        orderFactory = new OrderFactory(
                feed,
                market,
                pendingOrders,
                portfolio,
                tradeHistory
        );
    }

    @Test
    void tradeMessageIsDispatchedToNotificationService() {

        // Arrange
        String message = "trade,BUY,AAPL,Market,100.00,2";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;


        try {
            System.setOut(new PrintStream(outputStream));

            // Act
            consoleNotify.sendNotification(message);

            // Assert
            String output = outputStream.toString().trim();
            assertTrue(output.contains("BUY order of type Market: AAPL At 100.00 for 2.00 shares."));

        } finally {

            System.setOut(originalOut);
        }
    }

    @Test
    void orderSuccessfulMessageIsDispatchedToNotificationService() {
        // Arrange
        FeedObject stock = new Stock("AAPL", 150.0, feedService, portfolio);

        when(feed.getObject("AAPL")).thenReturn(stock);
        when(portfolio.getCashBalance()).thenReturn(200.0);
        when(portfolio.getNotifications()).thenReturn(consoleNotify);

        // Act
        Order order = orderFactory.createOrder("Market", "buy", "AAPL", 150.0, 1);

    //    assertEquals(consoleNotify.sendNotification(String.format("balance,%.2f,%.2f", 150.0, 1.0)), "Market order of type buy: AAPL At 150.00 for 1.00 shares.");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {

            // Act
            consoleNotify.sendNotification(
                    String.format("trade,%s", order.getOrder())
            );

            // Assert
            String output = outputStream.toString().trim();
            assertTrue(output.contains("Market order of type buy: AAPL At 150.00 for 1.00 shares."));

        } finally {

            System.setOut(originalOut);
        }
    }

    @Test
    void orderUnsuccessfulMessageIsDispatchedToNotificationService() {
        // Arrange
        FeedObject stock = new Stock("AAPL", 150.0, feedService, portfolio);

        when(feed.getObject("AAPL")).thenReturn(stock);
        when(portfolio.getCashBalance()).thenReturn(100.0);
        when(portfolio.getNotifications()).thenReturn(consoleNotify);

        // Act
        Order order = orderFactory.createOrder("Market", "buy", "AAPL", 150.0, 1);

        // Assert
        assertNull(order);

        //Arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {

            // Act
            consoleNotify.sendNotification(
                    String.format("balance,%.2f,%.2f,%.2f", 150.0, 1.0,100.0)
            );

            // Assert
            String output = outputStream.toString().trim();
            System.setOut(originalOut);
            assertTrue(output.contains("Error: Order couldn't be filled as you have insufficient funds. Balance: 100.00 Order Cost: 150.0"));

        } finally {

            System.setOut(originalOut);
        }
    }

    @Test
    void tradeMessageIsDispatchedToNotificationServiceSMS() {

        // Arrange
        SMSNotify sms = new SMSNotify(notification);
        String message = "trade,BUY,AAPL,Market,100.00,2";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;


        try {
            System.setOut(new PrintStream(outputStream));

            // Act
            sms.sendNotification(message);

            // Assert
            String output = outputStream.toString().trim();
            assertTrue(output.equals("SMS Message: BUY order of type Market: AAPL At 100.00 for 2.00 shares."));

        } finally {

            System.setOut(originalOut);
        }
    }

    @Test
    void orderSuccessfulMessageIsDispatchedToNotificationServiceSMS() {
        // Arrange
        FeedObject stock = new Stock("AAPL", 150.0, feedService, portfolio);
        SMSNotify sms = new SMSNotify(notification);
        when(feed.getObject("AAPL")).thenReturn(stock);
        when(portfolio.getCashBalance()).thenReturn(200.0);
        when(portfolio.getNotifications()).thenReturn(sms);

        // Act
        Order order = orderFactory.createOrder("Market", "buy", "AAPL", 150.0, 1);

        //    assertEquals(consoleNotify.sendNotification(String.format("balance,%.2f,%.2f", 150.0, 1.0)), "Market order of type buy: AAPL At 150.00 for 1.00 shares.");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {

            // Act
            sms.sendNotification(
                    String.format("trade,%s", order.getOrder())
            );

            // Assert
            String output = outputStream.toString().trim();
            assertTrue(output.equals("SMS Message: Market order of type buy: AAPL At 150.00 for 1.00 shares."));

        } finally {

            System.setOut(originalOut);
        }
    }

    @Test
    void orderUnsuccessfulMessageIsDispatchedToNotificationServiceEmail() {
        // Arrange
        FeedObject stock = new Stock("AAPL", 150.0, feedService, portfolio);
        EmailNotify email = new EmailNotify(notification);
        when(feed.getObject("AAPL")).thenReturn(stock);
        when(portfolio.getCashBalance()).thenReturn(100.0);
        when(portfolio.getNotifications()).thenReturn(email);

        // Act
        Order order = orderFactory.createOrder("Market", "buy", "AAPL", 150.0, 1);

        // Assert
        assertNull(order);

        //Arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {

            // Act
            email.sendNotification(
                    String.format("balance,%.2f,%.2f,%.2f", 150.0, 1.0,100.0)
            );

            // Assert
            String output = outputStream.toString().trim();
            System.setOut(originalOut);
            assertTrue(output.equals("Email Message: Dear user, \n Your order couldn't be filled as you have insufficient funds. Balance: 100.00 Order Cost: 150.0"));

        } finally {

            System.setOut(originalOut);
        }
    }

    @Test
    void tradeMessageIsDispatchedToNotificationServiceEmail() {

        // Arrange
        EmailNotify email = new EmailNotify(notification);
        String message = "trade,BUY,AAPL,Market,100.00,2";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;


        try {
            System.setOut(new PrintStream(outputStream));

            // Act
            email.sendNotification(message);

            // Assert
            String output = outputStream.toString().trim();
            assertTrue(output.equals("Email Message: Dear user, \n BUY order of type Market: AAPL At 100.00 for 2.00 shares."));

        } finally {

            System.setOut(originalOut);
        }
    }

    @Test
    void orderSuccessfulMessageIsDispatchedToNotificationServiceEmail() {
        // Arrange
        FeedObject stock = new Stock("AAPL", 150.0, feedService, portfolio);
        EmailNotify email = new EmailNotify(notification);
        when(feed.getObject("AAPL")).thenReturn(stock);
        when(portfolio.getCashBalance()).thenReturn(200.0);
        when(portfolio.getNotifications()).thenReturn(email);

        // Act
        Order order = orderFactory.createOrder("Market", "buy", "AAPL", 150.0, 1);

        //    assertEquals(consoleNotify.sendNotification(String.format("balance,%.2f,%.2f", 150.0, 1.0)), "Market order of type buy: AAPL At 150.00 for 1.00 shares.");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {

            // Act
            email.sendNotification(
                    String.format("trade,%s", order.getOrder())
            );

            // Assert
            String output = outputStream.toString().trim();
            assertTrue(output.equals("Email Message: Dear user, \n Market order of type buy: AAPL At 150.00 for 1.00 shares."));

        } finally {

            System.setOut(originalOut);
        }
    }



    @Test
    void orderUnsuccessfulMessageIsDispatchedToNotificationServiceSMS() {
        // Arrange
        FeedObject stock = new Stock("AAPL", 150.0, feedService, portfolio);
        SMSNotify sms = new SMSNotify(notification);
        when(feed.getObject("AAPL")).thenReturn(stock);
        when(portfolio.getCashBalance()).thenReturn(100.0);
        when(portfolio.getNotifications()).thenReturn(sms);

        // Act
        Order order = orderFactory.createOrder("Market", "buy", "AAPL", 150.0, 1);

        // Assert
        assertNull(order);

        //Arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {

            // Act
            sms.sendNotification(
                    String.format("balance,%.2f,%.2f,%.2f", 150.0, 1.0,100.0)
            );

            // Assert
            String output = outputStream.toString().trim();
            System.setOut(originalOut);
            assertTrue(output.equals("SMS Message: Your order couldn't be filled as you have insufficient funds. Balance: 100.00 Order Cost: 150.0"));

        } finally {

            System.setOut(originalOut);
        }
    }

    @Test
    void tradeMessageIsDispatchedToNotificationServiceDashboard() {

        // Arrange
        DashboardNotification dashboard = new DashboardNotification(orderService, 1, notification);
        String message = "trade,BUY,AAPL,Market,100.00,2";

        // Act
        dashboard.sendNotification(message);

        // Assert
        verify(orderService).sendNotification("1,1");

    }

    @Test
    void tradeMessageNotDispatchedDashboard() {

        // Arrange
        DashboardNotification dashboard = new DashboardNotification(orderService, 1, notification);
        String message = String.format("balance,%.2f,%.2f,%.2f", 150.0, 1.0,100.0);

        // Act
        dashboard.sendNotification(message);

        // Assert
        verify(orderService, never()).sendNotification("1,1");

    }

    @Test
    void decoratorChainCallsAllNotifications() {
        // Arrange
        BaseNotification base = Mockito.spy(new BaseNotification());
        ConsoleNotify console = Mockito.spy(new ConsoleNotify(base));
        SMSNotify sms = Mockito.spy(new SMSNotify(console));
        EmailNotify email = Mockito.spy(new EmailNotify(sms));
        DashboardNotification dashboard = Mockito.spy(new DashboardNotification(orderService, 1, email));

        String message = "Test message";

        // Act
        dashboard.sendNotification(message);

        // Assert
        Mockito.verify(dashboard).sendNotification(message);
        Mockito.verify(email).sendNotification(message);
        Mockito.verify(sms).sendNotification(message);
        Mockito.verify(console).sendNotification(message);
        Mockito.verify(base).sendNotification(message);

    }
}
