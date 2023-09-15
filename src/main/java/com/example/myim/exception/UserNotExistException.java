package com.example.myim.exception;

/**
 * 异常类
 * 用户不存在
 * @author YJL
 */
public class UserNotExistException extends RuntimeException{

    public UserNotExistException(String message){
        super(message);
    }

}
