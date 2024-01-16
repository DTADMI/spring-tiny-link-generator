package ca.dtadmi.tinylink.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
public class FirestoreExecutionException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final int errorCode;

    public FirestoreExecutionException(String errorMessage){
        super(errorMessage);
        this.errorCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
    public FirestoreExecutionException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
    public FirestoreExecutionException(Throwable cause) {
        super(cause);
        this.errorCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
    public FirestoreExecutionException(String errorMessage, int errorCode){
        super(errorMessage);
        this.errorCode = errorCode;
    }
    public FirestoreExecutionException(String errorMessage, int errorCode, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
    }
    public FirestoreExecutionException(int errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }
}
