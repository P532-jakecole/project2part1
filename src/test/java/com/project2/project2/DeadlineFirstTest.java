package com.project2.project2;

import com.project2.Factory.ImagingOrder;
import com.project2.Factory.LabOrder;
import com.project2.Factory.MedicationOrder;
import com.project2.Factory.Order;
import com.project2.OrderAccess;
import com.project2.Strategy.DeadlineFirst;
import com.project2.Strategy.LoadBalancing;
import com.project2.Strategy.PriorityFirst;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeadlineFirstTest {

    @Mock
    OrderAccess orderAccess;

    @Test
    void addingToEmptyOrderList() {
        // Arrange
        DeadlineFirst df = new DeadlineFirst(orderAccess);
        when(orderAccess.listPendingOrders()).thenReturn(new ArrayList<>());
        String priority = "STAT";
        LocalDateTime time = LocalDateTime.now();
        String type = "Lab";

        // Act
        int index = df.getPosition(priority, time, type);

        // Assert
        assertEquals(0, index);
    }

    @Test
    void addingToBackOfOrderList() {
        // Arrange
        DeadlineFirst df = new DeadlineFirst(orderAccess);
        Order order1 = new LabOrder(1, "Jake", "Doctor", "Test", "ROUTINE");
        Order order2 = new LabOrder(2, "Jake", "Doctor", "Test2", "ROUTINE");

        ArrayList<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);

        when(orderAccess.listPendingOrders()).thenReturn(orders);
        String priority = "ROUTINE";
        LocalDateTime time = LocalDateTime.now();
        String type = "Lab";

        // Act
        int index = df.getPosition(priority, time, type);

        // Assert
        assertEquals(2, index);
    }

    @Test
    void addingToFrontOfOrderList() {
        // Arrange
        DeadlineFirst df = new DeadlineFirst(orderAccess);
        Order order1 = new MedicationOrder(1, "Jake", "Doctor", "Test", "STAT");
        Order order2 = new ImagingOrder(2, "Jake", "Doctor", "Test2", "STAT");

        ArrayList<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);

        when(orderAccess.listPendingOrders()).thenReturn(orders);
        String priority = "STAT";
        LocalDateTime time = LocalDateTime.now();
        String type = "Lab";

        // Act
        int index = df.getPosition(priority, time, type);

        // Assert
        assertEquals(0, index);
    }

    @Test
    void addingToMiddleOfOrderList() {
        // Arrange
        DeadlineFirst df = new DeadlineFirst(orderAccess);
        Order order1 = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");
        Order order2 = new ImagingOrder(2, "Jake", "Doctor", "Test2", "STAT");

        ArrayList<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);

        when(orderAccess.listPendingOrders()).thenReturn(orders);
        String priority = "STAT";
        LocalDateTime time = LocalDateTime.now();
        String type = "Lab";

        // Act
        int index = df.getPosition(priority, time, type);

        // Assert
        assertEquals(1, index);
    }

    @Test
    void addingToMiddleOfOrderList2() {
        // Arrange
        DeadlineFirst df = new DeadlineFirst(orderAccess);
        Order order1 = new LabOrder(1, "Jake", "Doctor", "Test", "STAT");
        Order order2 = new LabOrder(2, "Jake", "Doctor", "Test2", "ROUTINE");

        ArrayList<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);

        when(orderAccess.listPendingOrders()).thenReturn(orders);
        String priority = "URGENT";
        LocalDateTime time = LocalDateTime.now();
        String type = "Lab";

        // Act
        int index = df.getPosition(priority, time, type);

        // Assert
        assertEquals(1, index);
    }

    @Test
    void SortingOrderList() {
        // Arrange
        DeadlineFirst df = new DeadlineFirst(orderAccess);
        ArgumentCaptor<ArrayList<Order>> captor = ArgumentCaptor.forClass(ArrayList.class);
        Order order1 = new MedicationOrder(1, "Jake", "Doctor", "Test", "URGENT");
        Order order2 = new LabOrder(2, "Jake", "Doctor", "Test2", "ROUTINE");
        Order order3 = new LabOrder(3, "Jake", "Doctor", "Test", "STAT");


        ArrayList<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        orders.add(order3);

        ArrayList<Order> expected = new ArrayList<>();
        expected.add(order3);
        expected.add(order1);
        expected.add(order2);

        when(orderAccess.listPendingOrders()).thenReturn(orders);

        // Act
        df.reorder();

        // Assert
        verify(orderAccess).setPendingOrders(captor.capture());
        ArrayList<Order> capturedPending = captor.getValue();

        assertEquals(3, capturedPending.size());
        assertEquals(expected, capturedPending);
    }

}
