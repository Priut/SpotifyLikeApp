package com.mongodb_example.main.utils.Exceptions.CustomExceptions;


public class SongAlreadyInPlaylistException extends Exception{

    private static final long serialVersionUID = 1L;

    public SongAlreadyInPlaylistException(String message){
        super(message);
    }
}