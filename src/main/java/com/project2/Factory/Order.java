package com.project2.Factory;

import com.project2.Observer.OrderObservable;

import java.time.LocalDateTime;

public interface Order extends OrderObservable {
    int getOrderID();
    String getType();
    String getPatientName();
    String getClinicianName();
    String getDescription();
    String getPriority();
    String getStatus();
    void setStatus(String status);
    LocalDateTime getTimestamp();

}
