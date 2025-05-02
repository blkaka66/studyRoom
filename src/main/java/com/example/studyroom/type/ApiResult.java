package com.example.studyroom.type;

public enum ApiResult {
    SUCCESS("0000", "OK"),
    FAIL("9999", "Fail"),
    MISSING_REQUIRED_FIELD("1000", "필수 필드 누락"),
    INVALID_EMAIL_FORMAT("1001", "잘못된 이메일 형식"),
    INVALID_REQUEST_DATA("1002", "유효하지 않은 요청 데이터"),
    ALREADY_EXIST_EMAIL("1003", "이미 존재하는 이메일 입니다"),
    ALREADY_EXIST_PHONE("1003", "이미 존재하는 전화번호 입니다"),
    AUTHENTICATION_FAILED("1004", "인증 실패 (잘못된 이메일/패스워드/전화번호/인증 코드 등)"),
    TOKEN_EXPIRED("1009", "Token is expired"),
    SHOP_NOT_FOUND("1005", "잘못된 샵정보"),
    ROOM_NOT_FOUND("1006", "잘못된 방정보"),
    SEAT_NOT_FOUND("1007", "잘못된 자리정보"),
    SEAT_ALREADY_OCCUPIED("1008", "주인있는 자리"),
    COUPON_NOT_FOUND("1009", "존재하지 않는 쿠폰"),
    TICKET_NOT_EXPIRED("1405", "만료되지않은티켓존재"),
    DATA_NOT_FOUND("3000", "데이터가없음"),
    TIMEOUT_EXCEEDED("3001", "유효시간초과"),
    TICKET_NOT_FOUND("3002", "구매한티켓없음"),
    EXPIRED_TICKET("3003", "만료된 티켓"),
    ALREADY_EXIST_ROOM("3004", "채팅방이 이미 존재합니다"),
    ALREADY_CLOSED_ROOM("3005", "닫힌 채팅방입니다"),

    ;
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
