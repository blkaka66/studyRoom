package com.example.studyroom.type;

public enum ToastVariant {
    DEFAULT("default"),
    DESTRUCTIVE("destructive"),
    SUCCESS("success"),
    WARNING("warning"),
    INFO("info"),
    CHAT("chat");

    private final String value;

    ToastVariant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
