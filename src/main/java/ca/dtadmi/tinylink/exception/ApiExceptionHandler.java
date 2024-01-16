package ca.dtadmi.tinylink.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<ApiRuntimeException> handleNoSuchElementException(NoSuchElementException exception) {
        this.logger.error("No such element exception: {}", exception.getMessage());
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ApiRuntimeException apiException = new ApiRuntimeException(httpStatus, exception.getMessage(), new Date(), exception.getCause());
        return new ResponseEntity<>(apiException, httpStatus);
    }
    @ExceptionHandler({ApiRuntimeException.class})
    public ResponseEntity<ApiRuntimeException> handleApiRuntimeException(ApiRuntimeException exception) {
        this.logger.error("Api runtime exception: {}", exception.getMessage());
        return new ResponseEntity<>(exception, exception.getHttpStatus());
    }
}
