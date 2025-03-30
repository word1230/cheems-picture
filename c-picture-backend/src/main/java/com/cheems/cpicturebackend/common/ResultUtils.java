package com.cheems.cpicturebackend.common;

import com.cheems.cpicturebackend.exception.ErrorCode;

/**
 * 返回结果封装类
 */
public class ResultUtils {

    /**
     * 成功
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data){
        return  new BaseResponse<>(0,data,"ok");
    }


    /**
     * 返回写好的状态码与信息
     * @param errorCode
     * @return
     * @param <T>
     */

    public static <T> BaseResponse<T> error(ErrorCode errorCode){
        return  new BaseResponse<>(errorCode);
    }



    /**
     * 失败时, 自定义返回的信息
     * @param errorCode
     * @param message
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode,String message){
        return  new BaseResponse<>(errorCode.getCode(),null,message);
    }

    /**
     * 失败时, 自定义code 与message
     * @param code
     * @param message
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> error(int code,String message){
        return  new BaseResponse<>(code,null,message);
    }




}
