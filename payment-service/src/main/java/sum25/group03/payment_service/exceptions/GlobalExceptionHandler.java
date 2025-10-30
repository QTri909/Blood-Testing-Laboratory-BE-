package sum25.group03.payment_service.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        var pd = problem(HttpStatus.BAD_REQUEST, "Validation failed", req);
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        fe -> fe.getField(),
                        Collectors.mapping(fe -> fe.getDefaultMessage(), Collectors.toList())));
        pd.setProperty("errors", errors);
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        var pd = problem(HttpStatus.BAD_REQUEST, "Constraint violation", req);
        pd.setProperty("violations", ex.getConstraintViolations().stream()
                .map(v -> Map.of("property", v.getPropertyPath().toString(), "message", v.getMessage()))
                .toList());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ProblemDetail> handleBadRequest(Exception ex, HttpServletRequest req) {
        var pd = problem(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        var pd = problem(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), req);
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @ExceptionHandler(VNPayInvalidSignatureException.class)
    public ResponseEntity<ProblemDetail> handleInvalidSig(VNPayInvalidSignatureException ex, HttpServletRequest req) {
        var pd = problem(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
        pd.setProperty("code", "INVALID_SIGNATURE");
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArg(IllegalArgumentException ex, HttpServletRequest req) {
        var pd = problem(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleIllegalState(IllegalStateException ex, HttpServletRequest req) {
        var pd = problem(HttpStatus.CONFLICT, ex.getMessage(), req);
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAny(Exception ex, HttpServletRequest req) {
        var pd = problem(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", req);
        pd.setProperty("exception", ex.getClass().getSimpleName());
        return ResponseEntity.status(pd.getStatus()).body(pd);
    }

    private ProblemDetail problem(HttpStatus status, String message, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, message);
        pd.setTitle(status.getReasonPhrase());
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty("errorId", UUID.randomUUID().toString());
        return pd;
    }
}
