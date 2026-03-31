package com.project2.project2;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.project2.Command.*;
import com.project2.Decorator.BaseNotification;
import com.project2.Decorator.ConsoleNotification;
import com.project2.Decorator.NotificationService;
import com.project2.Factory.*;
import com.project2.OrderAccess;
import com.project2.Strategy.TriagingEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class CommandTest {
    @Mock
    OrderAccess orderAccess;

    @Mock
    TriagingEngine triagingEngine;

    @Mock
    CommandLog commandLog;

    OrderFactory orderFactory;
    NotificationService notificationService;

    @BeforeEach
    void setup() {
        SimpMessagingTemplate mockTemplate = Mockito.mock(SimpMessagingTemplate.class);
        notificationService = new ConsoleNotification(new BaseNotification(), mockTemplate);

        orderFactory = new OrderFactory(
                orderAccess,
                notificationService,
                triagingEngine,
                commandLog
        );
    }

    @Test
    void submitCommandTest() {
        // Arrange
        Order order = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");
        Command submitCommand = new LabOrderSubmitCommand(order, triagingEngine, orderAccess, notificationService, commandLog);
        String actor = "Doctor";
        when(triagingEngine.getPosition(order.getPriority(), order.getTimestamp())).thenReturn(0);

        // Act
        submitCommand.execute(actor);

        // Assert
        verify(commandLog).addLog(order, actor, "submit");
        verify(triagingEngine).getPosition(order.getPriority(), order.getTimestamp());
        verify(orderAccess).saveOrder(0, order);
        assertEquals(order.getStatus(), "PENDING");

    }

    @Test
    void claimCommandTest() {
        // Arrange
        Order order = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");
        Command claimCommand = new LabOrderClaimCommand(order, notificationService, commandLog);
        String actor = "Lab";

        // Act
        claimCommand.execute(actor);

        // Assert
        verify(commandLog).addLog(order, actor, "claim");
        assertEquals(order.getStatus(), "IN_PROGRESS");

    }

    @Test
    void completeCommandTest() {
        // Arrange
        Order order = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");
        Command completeCommand = new LabOrderCompleteCommand(order, notificationService, commandLog);
        String actor = "Lab";

        // Act
        completeCommand.execute(actor);

        // Assert
        verify(commandLog).addLog(order, actor, "complete");
        assertEquals(order.getStatus(), "COMPLETED");

    }

    @Test
    void cancelCommandTest() {
        // Arrange
        Order order = new ImagingOrder(1, "Jake", "Doctor", "Test", "STAT");
        Command cancelCommand = new ImagingOrderCancelCommand(order, orderAccess, notificationService, commandLog);
        String actor = "Doctor";

        // Act
        cancelCommand.execute(actor);

        // Assert
        verify(commandLog).addLog(order, actor, "cancel");
        verify(orderAccess).removeOrder(order);
        assertEquals(order.getStatus(), "CANCELLED");
    }

    @Test
    void cancelCommandFailTest() {
        // Arrange
        Order order = new MedicationOrder(1, "Jake", "Doctor", "Test", "STAT");
        Command cancelCommand = new MedicationOrderCancelCommand(order, orderAccess, notificationService, commandLog);
        String actor = "Doctor";
        order.setStatus("IN_PROGRESS");

        // Act
        cancelCommand.execute(actor);

        // Assert
        verify(commandLog, never()).addLog(order, actor, "cancel");
        verify(orderAccess, never()).removeOrder(order);
        assertEquals(order.getStatus(), "IN_PROGRESS");
    }
}
