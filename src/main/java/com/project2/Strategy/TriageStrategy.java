package com.project2.Strategy;

import java.time.LocalDateTime;

public interface TriageStrategy {
    int getPosition(String priority, LocalDateTime timestamp);
}

