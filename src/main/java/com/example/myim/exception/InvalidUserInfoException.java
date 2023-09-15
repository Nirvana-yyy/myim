package com.example.myim.exception;

public class InvalidUserInfoException extends RuntimeException{

    public InvalidUserInfoException(String message){
        super(message);
    }

}
