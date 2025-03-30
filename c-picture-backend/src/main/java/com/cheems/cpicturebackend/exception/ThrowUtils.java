package com.cheems.cpicturebackend.exception;

public class ThrowUtils {

    private boolean condition ;

    /**
     *条件成立抛异常
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition,RuntimeException runtimeException) {
        if (condition){
            throw runtimeException;
        }
    }


    /**
     * 进一步封装, 直接传递errorcode
     * @param condition
     * @param errorCode
     * @throws RuntimeException
     */
    public static void throwIf(boolean condition,ErrorCode errorCode) throws RuntimeException {
        ThrowUtils.throwIf(condition,new BusinessException(errorCode)) ;
    }


    /***
     * 不止要使用errorcode 还要传递更详细的信息
     * @param condition
     * @param errorCode
     * @throws RuntimeException
     */
    public static void throwIf(boolean condition,ErrorCode errorCode,String message) throws RuntimeException {
        ThrowUtils.throwIf(condition,new BusinessException(errorCode,message)) ;
    }
}
