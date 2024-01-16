package ca.dtadmi.tinylink.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApiRuntimeException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final HttpStatus httpStatus;
    private final String errorMessage;
    private final Date errorDate;
    private final Throwable cause;

    public ApiRuntimeException(String errorMessage, Date errorDate, Throwable cause) {
        super(errorMessage);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.errorMessage = errorMessage;
        this.errorDate = errorDate;
        this.cause = cause;
    }
    public ApiRuntimeException(HttpStatus httpStatus, String errorMessage, Date errorDate) {
        super(errorMessage);
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
        this.errorDate = errorDate;
        this.cause = new RuntimeException(errorMessage);
    }

    public ApiRuntimeException(HttpStatus httpStatus, String message, Date date, Throwable cause) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorMessage = message;
        this.errorDate = date;
        this.cause = cause;
    }
}
