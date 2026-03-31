package com.project2.Command;

import com.project2.Factory.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class CommandLog {
    HashMap<Integer, ArrayList<Command>> activeCommands = new HashMap<>();

    ArrayList<String[]> commandLog = new ArrayList<>();

    public void addLog(Order command, String actor, String commandType) {
        String[] log = new String[4];
        log[0] = command.getTimestamp().toString();
        log[1] = commandType;
        log[2] = String.valueOf(command.getOrderID());
        log[3] = actor;

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

}
