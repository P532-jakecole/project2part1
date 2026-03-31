package com.project2.Strategy;

import com.project2.OrderAccess;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TriagingEngine {
    private OrderAccess orderAccess;
    private TriageStrategy triageStrategy;

    public TriagingEngine(OrderAccess orderAccess) {
        this.orderAccess = orderAccess;
        triageStrategy = new PriorityFirst(orderAccess);
    }

    public int getPosition(String priority, LocalDateTime timestamp){
        return triageStrategy.getPosition(priority, timestamp);
    }

    public void updateTriage(String triage){
        switch(triage.toLowerCase()){
            case "priorityfirst":
                triageStrategy = new PriorityFirst(orderAccess);
                break;
            default:
                return;
        }
    }
}
