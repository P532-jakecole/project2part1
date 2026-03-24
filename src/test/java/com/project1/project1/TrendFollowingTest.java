package com.project1.project1;

import com.project1.project1.Pricing.MeanReversion;
import com.project1.project1.Pricing.Observer;
import com.project1.project1.Pricing.RandomWalk;
import com.project1.project1.Pricing.TrendFollowing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrendFollowingTest {

    @Mock
    Observer observer;

    @Mock
    Random random;

    @Test
    void updatePriceWithNoChange() {

        // Arrange
        when(observer.getPrice()).thenReturn(100.0);
        when(random.nextDouble()).thenReturn(0.5);

        TrendFollowing model = new TrendFollowing(random);

        // Act
        double price = model.updatePrice(observer);

        // Assert
        assertEquals(100.0, price);
    }

    @Test
    void updatePriceWithPositiveChange() {

        // Arrange
        when(observer.getPrice()).thenReturn(100.0);
        when(random.nextDouble()).thenReturn(0.8);

        TrendFollowing model = new TrendFollowing(random);

        // Act
        double price = model.updatePrice(observer);

        // Assert
        assertEquals(100.6, price);
    }

    @Test
    void updatePriceWithNegativeChange() {

        // Arrange
        when(observer.getPrice()).thenReturn(100.0);
        when(random.nextDouble()).thenReturn(0.1);

        TrendFollowing model = new TrendFollowing(random);

        // Act
        double price = model.updatePrice(observer);

        // Assert
        assertEquals(99.2, price);
    }

    @Test
    void updatePriceWithContinuousPositiveChange() {

        // Arrange
        AtomicReference<Double> priceRef = new AtomicReference<>(100.0);
        when(observer.getPrice()).thenAnswer(invocation -> priceRef.get());
        when(random.nextDouble()).thenReturn(0.8);

        TrendFollowing model = new TrendFollowing(random);

        // Act
        for (int i = 0; i < 5; i++) {
            double price = model.updatePrice(observer);
            priceRef.set(price);
        }

        // Assert
        assertEquals(103.182, Math.round(observer.getPrice() * 1000) / 1000.0);

        // Act
        for (int i = 0; i < 5; i++) {
            double price = model.updatePrice(observer);
            priceRef.set(price);
        }

        // Assert
        assertEquals(106.508, Math.round(observer.getPrice() * 1000) / 1000.0);
    }

    @Test
    void updatePriceWithContinuousNegativeChange() {

        // Arrange
        AtomicReference<Double> priceRef = new AtomicReference<>(100.0);
        when(observer.getPrice()).thenAnswer(invocation -> priceRef.get());
        when(random.nextDouble()).thenReturn(0.1);

        TrendFollowing model = new TrendFollowing(random);

        // Act
        for (int i = 0; i < 5; i++) {
            double price = model.updatePrice(observer);
            priceRef.set(price);
        }

        // Assert
        assertEquals(95.757, Math.round(observer.getPrice() * 1000) / 1000.0);

        // Act
        for (int i = 0; i < 5; i++) {
            double price = model.updatePrice(observer);
            priceRef.set(price);
        }

        // Assert
        assertEquals(91.322, Math.round(observer.getPrice() * 1000) / 1000.0);
    }
}