package com.project2.project2;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import com.project2.Command.CommandLog;
import com.project2.Decorator.*;
import com.project2.Factory.LabOrder;
import com.project2.Factory.Order;
import com.project2.OrderAccess;
import com.project2.Strategy.TriagingEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@ExtendWith(MockitoExtension.class)
class NotificationTest {

    @Mock
    OrderAccess orderAccess;

    OrderProcess orderProcess;
    BaseNotification notification;
    ConsoleNotification consoleNotification;


    @Test
    void testingNotificationChainFiring() {
        // Arrange
        SimpMessagingTemplate mockTemplate = Mockito.mock(SimpMessagingTemplate.class);

        // Manually recreate configuration
        NotificationService base = Mockito.spy(new BaseNotification());
        NotificationService chain = Mockito.spy(new ConsoleNotification(base, mockTemplate));

        NotificationChain notificationChain = new NotificationChain();
        notificationChain.setActiveNotifications(chain);

        Order order = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");
        String action = "pending";

        // Act
        notificationChain.getActiveNotifications().notify(order, action);

        // Verify
        Mockito.verify(base).notify(order, action);
        Mockito.verify(chain).notify(order, action);

    }

    @Test
    void orderSubmitMessageSent() {
        // Arrange
        Order order = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");

        SimpMessagingTemplate mockTemplate = Mockito.mock(SimpMessagingTemplate.class);
        NotificationService base = Mockito.spy(new BaseNotification());
        NotificationService console = Mockito.spy(new ConsoleNotification(base, mockTemplate));



        // Act
        console.notify(order, "pending");

        // Assert
        String finalMessage = "Order 1 Successfully Submitted by Doctor\n";
        verify(mockTemplate).convertAndSend("/order/logs", finalMessage);
    }

    @Test
    void orderClaimMessageSent() {
        // Arrange
        Order order = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");

        SimpMessagingTemplate mockTemplate = Mockito.mock(SimpMessagingTemplate.class);
        NotificationService base = Mockito.spy(new BaseNotification());
        NotificationService console = Mockito.spy(new ConsoleNotification(base, mockTemplate));



        // Act
        console.notify(order, "in_progress");

        // Assert
        String finalMessage = "Order 1 Successfully Claimed\n";
        verify(mockTemplate).convertAndSend("/order/logs", finalMessage);
    }

    @Test
    void orderCompleteMessageSent() {
        // Arrange
        Order order = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");

        SimpMessagingTemplate mockTemplate = Mockito.mock(SimpMessagingTemplate.class);
        NotificationService base = Mockito.spy(new BaseNotification());
        NotificationService console = Mockito.spy(new ConsoleNotification(base, mockTemplate));



        // Act
        console.notify(order, "completed");

        // Assert
        String finalMessage = "Order 1 Successfully Completed\n";
        verify(mockTemplate).convertAndSend("/order/logs", finalMessage);
    }

    @Test
    void orderCancelMessageSent() {
        // Arrange
        Order order = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");

        SimpMessagingTemplate mockTemplate = Mockito.mock(SimpMessagingTemplate.class);
        NotificationService base = Mockito.spy(new BaseNotification());
        NotificationService console = Mockito.spy(new ConsoleNotification(base, mockTemplate));



        // Act
        console.notify(order, "cancelled");

        // Assert
        String finalMessage = "Order 1 Successfully Cancelled\n";
        verify(mockTemplate).convertAndSend("/order/logs", finalMessage);
    }

    @Test
    void orderCancelErrorMessageSent() {
        // Arrange
        Order order = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");
        order.setStatus("IN_PROGRESS");

        SimpMessagingTemplate mockTemplate = Mockito.mock(SimpMessagingTemplate.class);
        NotificationService base = Mockito.spy(new BaseNotification());
        NotificationService console = Mockito.spy(new ConsoleNotification(base, mockTemplate));



        // Act
        console.notify(order, "cancel error");

        // Assert
        String finalMessage = "Error cancelling order 1. Status of order is IN_PROGRESS\n";
        verify(mockTemplate).convertAndSend("/order/logs", finalMessage);
    }
}
