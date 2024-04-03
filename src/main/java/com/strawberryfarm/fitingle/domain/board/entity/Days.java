package com.strawberryfarm.fitingle.domain.board.entity;

public enum Days {
    WEEKDAY("평일"),
    WEEKEND("주말"),
    ANYDAY("상관없음");

    private final String label;

    Days(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Days fromLabel(String label) {
        for (Days day : Days.values()) {
            if (day.getLabel().equals(label)) {
                return day;
            }
        }
        throw new IllegalArgumentException("Unknown label: " + label);
    }
}
