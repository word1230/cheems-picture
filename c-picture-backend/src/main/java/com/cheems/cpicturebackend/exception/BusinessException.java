package com.cheems.cpicturebackend.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final  int code;


    /**
     * 不会用到, 为了final
     * @param code
     * @param message
     */
    public BusinessException( int code,  String message) {
        super(message);
        this.code = code;
    }


    /**
     * 出现错误, 且需要自定义信息
     * @param errorCode
     * @param message
     */
    public BusinessException( ErrorCode errorCode,  String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    /**
     * 不需要将详细错误传递出去, 使用枚举类中的消息
     * @param errorCode
     */
    public BusinessException( ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }





}
