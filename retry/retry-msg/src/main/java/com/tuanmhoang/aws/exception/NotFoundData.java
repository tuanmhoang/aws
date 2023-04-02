package com.tuanmhoang.aws.exception;

public class NotFoundData extends RuntimeException{

    public NotFoundData(String message){
        super((message));
    }
}
