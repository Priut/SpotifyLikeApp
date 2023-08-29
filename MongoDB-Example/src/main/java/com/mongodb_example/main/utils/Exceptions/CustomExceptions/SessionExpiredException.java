package com.mongodb_example.main.utils.Exceptions.CustomExceptions;

public class SessionExpiredException extends Exception{

    private static final long serialVersionUID = 1L;

    public SessionExpiredException(String message){
        super(message);
    }
}