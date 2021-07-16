package com.siegfried.blog.exception;

/**
 * 自定义异常类
 * Created by zy_zhu on 2021/5/8.
 */
public class ServeException extends RuntimeException{
    public ServeException(String message){
        super(message);
    }
}
