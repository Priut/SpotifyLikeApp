package MusicDBSpringService.View.Exceptions.CustomExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class AlbumIDInvalidException extends Exception{

    private static final long serialVersionUID = 1L;

    public AlbumIDInvalidException(String message){
        super(message);
    }
}