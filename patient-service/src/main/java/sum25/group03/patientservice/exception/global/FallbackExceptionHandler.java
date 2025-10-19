package sum25.group03.patientservice.exception.global;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sum25.group03.patientservice.dtos.response.ErrorResponse;
import sum25.group03.patientservice.exception.BusinessLogicException;

@RestControllerAdvice
public class FallbackExceptionHandler {

    // fallback exception handler
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(BusinessLogicException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .reason(ex.getReason())
                .build();
        return ResponseEntity
                .status(400)
                .body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .reason("RUNTIME_EXCEPTION")
                .build();
        return ResponseEntity
                .status(500)
                .body(errorResponse);
    }
}
