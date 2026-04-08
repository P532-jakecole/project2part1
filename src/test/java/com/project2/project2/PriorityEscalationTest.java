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
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@ExtendWith(MockitoExtension.class)
class PriorityEscalationTest {
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
    void upgradePriorityTest() {
        // Arrange
        Order order = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");
        Command submitCommand = new LabOrderSubmitCommand(order, triagingEngine, orderAccess, notificationService, commandLog);
        String actor = "Doctor";
        when(triagingEngine.getPosition(order.getPriority(), order.getTimestamp(), order.getType())).thenReturn(0);
        when(commandLog.getEscalation("Lab")).thenReturn(true);

        // Act
        submitCommand.execute(actor);

        // Assert

        verify(commandLog).addLog(order, actor, "submit", "No Change", "0");
        verify(orderAccess).saveOrder(0, order);
        assertEquals(order.getPriority(), "STAT");

        // Arrange
        Order order2 = new LabOrder(2, "Jake", "Doctor", "Test", "URGENT");
        Command submitCommand2 = new LabOrderSubmitCommand(order2, triagingEngine, orderAccess, notificationService, commandLog);

        // Act
        submitCommand2.execute(actor);

        // Assert
        verify(commandLog).addLog(order2, actor, "submit", "Escalated", "N/A");
        assertEquals(order2.getPriority(), "STAT");

    }

    @Test
    void notUpgradeDepartmentPriorityTest() {
        // Arrange
        Order order = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");
        Command submitCommand = new LabOrderSubmitCommand(order, triagingEngine, orderAccess, notificationService, commandLog);
        String actor = "Doctor";
        when(triagingEngine.getPosition(order.getPriority(), order.getTimestamp(), order.getType())).thenReturn(0);
        when(commandLog.getEscalation("Imaging")).thenReturn(false);

        // Act
        submitCommand.execute(actor);

        // Assert

        verify(commandLog).addLog(order, actor, "submit", "No Change", "0");
        verify(orderAccess).saveOrder(0, order);
        assertEquals(order.getPriority(), "STAT");

        // Arrange
        Order order2 = new ImagingOrder(2, "Jake", "Doctor", "Test", "URGENT");
        Command submitCommand2 = new ImagingOrderSubmitCommand(order2, triagingEngine, orderAccess, notificationService, commandLog);

        // Act
        submitCommand2.execute(actor);

        // Assert
        verify(commandLog).addLog(order2, actor, "submit");
        assertEquals(order2.getPriority(), "URGENT");

    }

    @Test
    void notUpgradeTimePriorityTest() {
        // Arrange

        Order order = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");
        Command submitCommand = new LabOrderSubmitCommand(order, triagingEngine, orderAccess, notificationService, commandLog);
        Order order2 = new LabOrder(2, "Jake", "Doctor2", "Test", "URGENT");
        Command submitCommand2 = new LabOrderSubmitCommand(order2, triagingEngine, orderAccess, notificationService, commandLog);

        doCallRealMethod().when(commandLog).setEscalation(any());
        doCallRealMethod().when(commandLog).getEscalation(any());

        String actor = "Doctor";
        when(triagingEngine.getPosition(order.getPriority(), order.getTimestamp(), order.getType())).thenReturn(0);
        try (MockedStatic<LocalDateTime> mockedTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            LocalDateTime fixedTime = LocalDateTime.of(2026, 4, 7, 10, 0);
            LocalDateTime fixedTime2 = LocalDateTime.of(2026, 4, 7, 10, 6);

            mockedTime.when(LocalDateTime::now).thenReturn(fixedTime);

            // Act
            submitCommand.execute(actor);

            // Assert

            verify(commandLog).addLog(order, actor, "submit", "No Change", "0");
            verify(orderAccess).saveOrder(0, order);
            assertEquals(order.getPriority(), "STAT");



            // Arrange
            mockedTime.when(LocalDateTime::now).thenReturn(fixedTime2);



            // Act
            submitCommand2.execute("Doctor2");

            // Assert
            assertEquals(order2.getPriority(), "URGENT");
        }

    }
}
