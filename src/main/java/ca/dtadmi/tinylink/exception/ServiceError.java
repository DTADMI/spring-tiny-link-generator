package ca.dtadmi.tinylink.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
public record ServiceError(HttpStatus httpStatus, String errorMessage, Date errorDate, Throwable cause) {

}
