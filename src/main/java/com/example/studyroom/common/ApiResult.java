package com.example.studyroom.common;

public enum ApiResult {
    SUCCESS("200", "Success"),
    NOT_FOUND("404", "Not Found");

    private String code;
    private String message;

    ApiResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
