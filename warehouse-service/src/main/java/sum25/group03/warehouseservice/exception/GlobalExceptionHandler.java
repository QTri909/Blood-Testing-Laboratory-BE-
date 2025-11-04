package sum25.group03.warehouseservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException ex, WebRequest req) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                req.getDescription(false),
                LocalDateTime.now());
    }
    @ExceptionHandler(DuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateException(DuplicateException ex, WebRequest req) {
        return new ErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                req.getDescription(false),
                LocalDateTime.now());
    }
    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidArgumentException(InvalidArgumentException ex, WebRequest req) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                req.getDescription(false),
                LocalDateTime.now());
    }
    @ExceptionHandler(MissingRequiredFieldsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequiredFieldsException(MissingRequiredFieldsException ex, WebRequest req) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                req.getDescription(false),
                LocalDateTime.now());
    }
}
