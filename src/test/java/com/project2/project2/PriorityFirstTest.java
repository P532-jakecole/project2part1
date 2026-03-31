package com.project2.project2;

import com.project2.Factory.LabOrder;
import com.project2.Factory.Order;
import com.project2.OrderAccess;
import com.project2.Strategy.PriorityFirst;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PriorityFirstTest {

    @Mock
    OrderAccess orderAccess;

    @Test
    void addingToEmptyOrderList() {
        // Arrange
        PriorityFirst pf = new PriorityFirst(orderAccess);
        when(orderAccess.listPendingOrders()).thenReturn(new ArrayList<>());
        String priority = "STAT";
        LocalDateTime time = LocalDateTime.now();

        // Act
        int index = pf.getPosition(priority, time);

        // Assert
        Assertions.assertEquals(0, index);
    }

    @Test
    void addingToFrontOfOrderList() {
        // Arrange
        PriorityFirst pf = new PriorityFirst(orderAccess);
        Order order1 = new LabOrder(1, "Jake", "Doctor", "Test", "ROUTINE");
        Order order2 = new LabOrder(1, "Jake", "Doctor", "Test2", "ROUTINE");

        ArrayList<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);

        when(orderAccess.listPendingOrders()).thenReturn(orders);
        String priority = "STAT";
        LocalDateTime time = LocalDateTime.now();

        // Act
        int index = pf.getPosition(priority, time);

        // Assert
        Assertions.assertEquals(0, index);
    }

    @Test
    void addingToBackOfOrderList(){
        // Arrange
        PriorityFirst pf = new PriorityFirst(orderAccess);
        Order order1 = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");
        Order order2 = new LabOrder(1, "Jake", "Doctor", "Test2", "URGENT");

        ArrayList<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);

        when(orderAccess.listPendingOrders()).thenReturn(orders);
        String priority = "URGENT";
        LocalDateTime time = LocalDateTime.now();

        // Act
        int index = pf.getPosition(priority, time);

        // Assert
        Assertions.assertEquals(2, index);
    }

    @Test
    void addingToMiddleOfOrderList() {
        // Arrange
        PriorityFirst pf = new PriorityFirst(orderAccess);
        Order order1 = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");
        Order order2 = new LabOrder(1, "Jake", "Doctor", "Test2", "ROUTINE");

        ArrayList<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);

        when(orderAccess.listPendingOrders()).thenReturn(orders);
        String priority = "STAT";
        LocalDateTime time = LocalDateTime.now();

        // Act
        int index = pf.getPosition(priority, time);

        // Assert
        Assertions.assertEquals(1, index);
    }

    @Test
    void addingToMiddleOfOrderListUrgent() {
        // Arrange
        PriorityFirst pf = new PriorityFirst(orderAccess);
        Order order1 = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");
        Order order2 = new LabOrder(1, "Jake", "Doctor", "Test2", "ROUTINE");

        ArrayList<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);

        when(orderAccess.listPendingOrders()).thenReturn(orders);
        String priority = "URGENT";
        LocalDateTime time = LocalDateTime.now();

        // Act
        int index = pf.getPosition(priority, time);

        // Assert
        Assertions.assertEquals(1, index);
    }
}
