package com.project2.project2;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.project2.Command.CommandLog;
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
class OrderFactoryTest {
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
    void labSubmitOrderSuccess() {

        // Arrange
        String[] input = {"lab", "Jake", "Doctor", "Trial", "STAT"};

        // Act
        Order order = orderFactory.create(input, 1);

        // Assert
        if (order == null) {
            fail();
        }
        assertEquals(order.getPatientName(), "Jake");
        assertEquals(order.getOrderID(), 1);
        assertEquals(order.getClass(), LabOrder.class);
    }

    @Test
    void imagingSubmitOrderSuccess() {

        // Arrange
        String[] input = {"Imaging", "Jake", "Doctor", "Trial", "STAT"};

        // Act
        Order order = orderFactory.create(input, 1);

        // Assert
        if (order == null) {
            fail();
        }
        assertEquals(order.getPatientName(), "Jake");
        assertEquals(order.getOrderID(), 1);
        assertEquals(order.getClass(), ImagingOrder.class);
    }

    @Test
    void medicationSubmitOrderSuccess() {

        // Arrange
        String[] input = {"MEDICATION", "Jake", "Doctor", "Trial", "STAT"};

        // Act
        Order order = orderFactory.create(input, 1);

        // Assert
        if (order == null) {
            fail();
        }
        assertEquals(order.getPatientName(), "Jake");
        assertEquals(order.getOrderID(), 1);
        assertEquals(order.getClass(), MedicationOrder.class);
    }

    @Test
    void WrongOrderTypeFail() {

        // Arrange
        String[] input = {"WRONG", "Jake", "Doctor", "Trial", "STAT"};

        // Act
        Order order = orderFactory.create(input, 1);

        // Assert
        if (order != null) {
            fail();
        }
    }

}