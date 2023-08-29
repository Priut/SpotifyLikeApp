package MusicDBSpringService.View.Exceptions.CustomExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class AlbuminAlbumException extends Exception{

    private static final long serialVersionUID = 1L;

    public AlbuminAlbumException(String message){
        super(message);
    }
}