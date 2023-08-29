package MusicDBSpringService.View.Exceptions.CustomExceptions;

public class PageInvalidException extends Exception{

    private static final long serialVersionUID = 1L;

    public PageInvalidException(String message){
        super(message);
    }
}