package com.project2.Factory;

import com.project2.Command.*;
import com.project2.Decorator.NotificationService;
import com.project2.OrderAccess;
import com.project2.Strategy.TriagingEngine;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class OrderFactory {
    private final NotificationService notificationService;
    private final OrderAccess orderAccess;
    private final TriagingEngine triagingEngine;
    private final CommandLog commandLog;

    public OrderFactory(OrderAccess orderAccess, NotificationService notificationService, TriagingEngine triagingEngine, CommandLog commandLog) {
        this.orderAccess = orderAccess;
        this.notificationService = notificationService;
        this.triagingEngine = triagingEngine;
        this.commandLog = commandLog;
    }

    public Order create(String[] order, int orderId){
        String type = order[0];
        ArrayList<Command> commands = new ArrayList<>();
        switch (type.toLowerCase()){
            case "lab":
                LabOrder lo = new LabOrder(orderId, order[1], order[2], order[3], order[4]);
                lo.registerObserver(notificationService);

                commands.add(new LabOrderSubmitCommand(lo, triagingEngine, orderAccess, notificationService, commandLog));
                commands.add(new LabOrderClaimCommand(lo, notificationService, commandLog));
                commands.add(new LabOrderCompleteCommand(lo, notificationService, commandLog));
                commands.add(new LabOrderCancelCommand(lo, orderAccess, notificationService, commandLog));
                commandLog.addCommands(orderId, commands);

                orderAccess.incrimentOrderId();
                return lo;
            case "medication":
                MedicationOrder mo = new MedicationOrder(orderId, order[1], order[2], order[3], order[4]);
                mo.registerObserver(notificationService);

                commands.add(new MedicationOrderSubmitCommand(mo, triagingEngine, orderAccess, notificationService, commandLog));
                commands.add(new MedicationOrderClaimCommand(mo, notificationService, commandLog));
                commands.add(new MedicationOrderCompleteCommand(mo, notificationService, commandLog));
                commands.add(new MedicationOrderCancelCommand(mo, orderAccess, notificationService, commandLog));
                commandLog.addCommands(orderId, commands);

                orderAccess.incrimentOrderId();
                return mo;
            case "imaging":
                ImagingOrder io = new ImagingOrder(orderId, order[1], order[2], order[3], order[4]);

                commands.add(new ImagingOrderSubmitCommand(io, triagingEngine, orderAccess, notificationService, commandLog));
                commands.add(new ImagingOrderClaimCommand(io, notificationService, commandLog));
                commands.add(new ImagingOrderCompleteCommand(io, notificationService, commandLog));
                commands.add(new ImagingOrderCancelCommand(io, orderAccess, notificationService, commandLog));
                commandLog.addCommands(orderId, commands);

                orderAccess.incrimentOrderId();
                io.registerObserver(notificationService);
                return io;
            default:
                return null;
        }
    }
}
