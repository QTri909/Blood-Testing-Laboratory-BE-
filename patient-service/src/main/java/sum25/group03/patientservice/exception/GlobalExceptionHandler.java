package sum25.group03.patientservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sum25.group03.patientservice.dtos.response.ErrorResponse;
import sum25.group03.patientservice.exception.clinical.note.ClinicalNoteNotFound;
import sum25.group03.patientservice.exception.medical.record.MedicalRecordNotFound;
import sum25.group03.patientservice.exception.user.snapshot.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder().message(ex.getMessage()).build();
        return ResponseEntity.status(404).body(errorResponse);
    }

    @ExceptionHandler(MedicalRecordNotFound.class)
    public ResponseEntity<ErrorResponse> handleMedicalRecordNotFound(MedicalRecordNotFound ex) {
        ErrorResponse errorResponse = ErrorResponse.builder().message(ex.getMessage()).build();
        return ResponseEntity.status(404).body(errorResponse);
    }

    @ExceptionHandler(ClinicalNoteNotFound.class)
    public ResponseEntity<ErrorResponse> handleClinicalNoteNotFound(ClinicalNoteNotFound ex) {
        ErrorResponse errorResponse = ErrorResponse.builder().message(ex.getMessage()).build();
        return ResponseEntity.status(404).body(errorResponse);
    }

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
