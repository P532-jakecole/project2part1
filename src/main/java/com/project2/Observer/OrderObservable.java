package com.project2.Observer;

public interface OrderObservable {
    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObserver(String event);
}
