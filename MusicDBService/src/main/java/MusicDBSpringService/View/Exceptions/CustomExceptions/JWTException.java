package MusicDBSpringService.View.Exceptions.CustomExceptions;

public class JWTException extends Exception{

    private static final long serialVersionUID = 1L;

    public JWTException(String message){
        super(message);
    }
}