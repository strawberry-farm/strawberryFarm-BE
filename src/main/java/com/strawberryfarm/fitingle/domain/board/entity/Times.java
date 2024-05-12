package com.strawberryfarm.fitingle.domain.board.entity;

import java.util.HashMap;
import java.util.Map;

public enum Times {
    DAWN("새벽"),
    MORNING("아침"),
    AFTERNOON("오후"),
    EVENING("저녁"),
    LATE_NIGHT("늦은 밤"),
    ANYTIME("상관없음");

    private static final Map<String, Times> labelToValueMap = new HashMap<>();
    private final String label;

    static {
        for (Times time : values()) {
            labelToValueMap.put(time.getLabel(), time);
        }
    }

    Times(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Times fromLabel(String label) {
        if (labelToValueMap.containsKey(label)) {
            return labelToValueMap.get(label);
        }
        throw new IllegalArgumentException("Unknown label: " + label);
    }
}
