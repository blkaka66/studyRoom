package com.example.studyroom.type;

public enum ApiResult {
    SUCCESS("0000", "OK"),
    FAIL("9999", "Fail"),
    ALREADY_EXIST_EMAIL("3000", "이미 존재하는 이메일 입니다"),
    BOT_NOT_FOUND("1404", "Bot resource not found");

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
