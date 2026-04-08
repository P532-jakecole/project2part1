package com.project2.Strategy;

import com.project2.Factory.Order;
import com.project2.OrderAccess;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class TriagingEngine {
    private final OrderAccess orderAccess;
    private TriageStrategy currentStrategy;
    private HashMap<String, TriageStrategy> departmentStrategies = new HashMap<>();
    private String currentDepartment;

    public TriagingEngine(OrderAccess orderAccess) {
        this.orderAccess = orderAccess;
        currentDepartment = "lab";
        currentStrategy = new PriorityFirst(orderAccess);
    }

    public int getPosition(String priority, LocalDateTime timestamp, String type){
        return currentStrategy.getPosition(priority, timestamp, type);
    }

    public void updateTriage(String triage){
        switch(triage.toLowerCase()){
            case "priorityfirst":
                currentStrategy = new PriorityFirst(orderAccess);
                currentStrategy.reorder();
                departmentStrategies.put(currentDepartment, currentStrategy);
                break;
            case "loadbalancing":
                currentStrategy = new LoadBalancing(orderAccess);
                currentStrategy.reorder();
                departmentStrategies.put(currentDepartment, currentStrategy);
                break;
            case "deadlinefirst":
                currentStrategy = new DeadlineFirst(orderAccess);
                currentStrategy.reorder();
                departmentStrategies.put(currentDepartment, currentStrategy);
                break;
        }
    }

    public void departmentChange(String department){
        currentDepartment = department;
        if(departmentStrategies.containsKey(department)){
            currentStrategy = departmentStrategies.get(department);
            currentStrategy.reorder();
        }else{
            currentStrategy = new PriorityFirst(orderAccess);
            currentStrategy.reorder();
            departmentStrategies.put(department, currentStrategy);
        }
    }
}
