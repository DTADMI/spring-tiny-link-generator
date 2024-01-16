package ca.dtadmi.tinylink.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ZookeeperExecutionException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final int errorCode;

    public ZookeeperExecutionException(String errorMessage){
        super(errorMessage);
        this.errorCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
    public ZookeeperExecutionException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
    public ZookeeperExecutionException(Throwable cause) {
        super(cause);
        this.errorCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
    public ZookeeperExecutionException(String errorMessage, int errorCode){
        super(errorMessage);
        this.errorCode = errorCode;
    }
    public ZookeeperExecutionException(String errorMessage, int errorCode, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
    }
    public ZookeeperExecutionException(int errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }
}
