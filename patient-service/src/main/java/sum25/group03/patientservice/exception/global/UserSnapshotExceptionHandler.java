package sum25.group03.patientservice.exception.global;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sum25.group03.patientservice.dtos.response.ErrorResponse;
import sum25.group03.patientservice.exception.user.snapshot.UserNotFoundException;

@RestControllerAdvice
public class UserSnapshotExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder().message(ex.getMessage()).build();
        return ResponseEntity.status(404).body(errorResponse);
    }
}
