package com.project2.Factory;

import com.project2.Observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MedicationOrder implements Order {
    private int orderID;
    private String patient;
    private String clinician;
    private String description;
    private String priority;
    private LocalDateTime orderDate;
    private String orderStatus;

    private ArrayList<Observer> observers = new ArrayList<>();

    public MedicationOrder(int orderID, String patient, String clinician, String description, String priority) {
        this.orderID = orderID;
        this.patient = patient;
        this.clinician = clinician;
        this.description = description;
        this.orderDate = LocalDateTime.now();
        this.priority = priority;
        this.orderStatus = "PENDING";
    }


    @Override
    public int getOrderID() {
        return orderID;
    }

    @Override
    public String getType() {
        return "Medication";
    }

    @Override
    public String getPatientName() {
        return patient;
    }

    @Override
    public String getClinicianName() {
        return clinician;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getPriority() {
        return priority;
    }

    @Override
    public String getStatus() {
        return orderStatus;
    }

    @Override
    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public void setStatus(String status) {
        this.orderStatus = status;
        notifyObserver(status);
    }

    @Override
    public LocalDateTime getTimestamp() {
        return orderDate;
    }


    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserver(String event) {
        for(Observer observer : observers) {
            observer.update(this, event);
        }
    }
}
