package com.example.studyroom.common;

import java.io.Serializable;

public class ResultEntity<T> implements Serializable {


    private String code = ApiResult.SUCCESS.getCode();
    private String message = ApiResult.SUCCESS.getMessage();
    private T data;

    public ResultEntity(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultEntity(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResultEntity ok(Object data) {
        return new ResultEntity(ApiResult.SUCCESS.getCode(), ApiResult.SUCCESS.getMessage(), data);
    }
}
