package exceptions;

public class OverDioException extends RuntimeException {

    public OverDioException(String message){
        super(message);
    }

    public OverDioException(String message, Throwable cause){
        super(message, cause);
    }
}
