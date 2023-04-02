package com.tuanmhoang.aws.exceptions;

public class ApiCallException extends RuntimeException{
    public ApiCallException(String message){
        super(message);
    }
}
