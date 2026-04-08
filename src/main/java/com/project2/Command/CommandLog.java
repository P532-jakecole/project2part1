package com.project2.Command;

import com.project2.Factory.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class CommandLog {
    HashMap<Integer, ArrayList<Command>> activeCommands = new HashMap<>();

    ArrayList<String[]> commandLog = new ArrayList<>();

    HashMap<String, LocalDateTime> escalation = new HashMap<>();
    HashMap<String, String> effectOrderId =  new HashMap<>();

    public void addLog(Order command, String actor, String commandType) {
        String[] log = new String[4];
        log[0] = command.getTimestamp().toString();
        log[1] = commandType;
        log[2] = String.valueOf(command.getOrderID());
        log[3] = actor;

        commandLog.add(log);
    }

    public void addLog(Order command, String actor, String commandType, String priorStatus, String effected) {
        String[] log = new String[6];
        log[0] = command.getTimestamp().toString();
        log[1] = commandType;
        log[2] = String.valueOf(command.getOrderID());
        log[3] = actor;
        log[4] = priorStatus;
        log[5] = effected;

        commandLog.add(log);
    }

    public void removeLog(Order command) {
        int orderID = command.getOrderID();
        removeCommand(orderID);

        String timestamp = command.getTimestamp().toString();
        for (int i = 0; i < commandLog.size(); i++) {
            if (commandLog.get(i)[0].equals(timestamp)) {
                commandLog.remove(i);
                break;
            }
        }
    }

    public ArrayList<String[]> getCommandLog() {
        return commandLog;
    }

    public void addCommands(int orderId, ArrayList<Command> commands) {
        activeCommands.put(orderId, commands);
    }

    public ArrayList<Command> getCommands(int orderId) {
        return activeCommands.get(orderId);
    }

    public Command getSubmitCommand(int orderId) {
        return activeCommands.get(orderId).get(0);
    }

    public Command getClaimCommand(int orderId) {
        return activeCommands.get(orderId).get(1);
    }

    public Command getCompleteCommand(int orderId) {
        return activeCommands.get(orderId).get(2);
    }

    public Command getCancelCommand(int orderId) {
        return activeCommands.get(orderId).get(3);
    }

    public void removeCommand(int orderId) {
        activeCommands.remove(orderId);
    }

    public void setEscalation(String type){
        if(escalation == null){
            escalation = new HashMap<>();
        }
        LocalDateTime now = LocalDateTime.now().plusMinutes(5);
        escalation.put(type, now);
    }

    public boolean getEscalation(String type){
        if(!escalation.containsKey(type)){
            return false;
        }
        LocalDateTime date = escalation.get(type);
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(date);
    }

    public void setEffected(String type, int orderId){
        effectOrderId.put(type, Integer.toString(orderId));
    }

    public void addEffected(String type){
        ArrayList<String[]> commands = getCommandLog();

        String orderId = effectOrderId.get(type);

        for(String[] args : commands){
            if(args.length > 4 && args[2].equals(orderId)){
                args[5] = Integer.toString(Integer.parseInt(args[5]) + 1);
            }
        }
    }

}
