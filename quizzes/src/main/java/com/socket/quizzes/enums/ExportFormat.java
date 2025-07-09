package com.socket.quizzes.enums;

public enum ExportFormat {
    CSV, JSON;

    public static ExportFormat from(String value) {
        return value != null && value.equalsIgnoreCase("csv") ? CSV : JSON;
    }
}