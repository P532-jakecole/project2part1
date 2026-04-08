package com.project2.Command;

import com.project2.Factory.Order;

public interface Command {
    void execute(String actor);
    void undo(String actor);
}
