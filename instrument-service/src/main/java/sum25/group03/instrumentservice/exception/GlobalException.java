package sum25.group03.instrumentservice.exception;

import io.micrometer.observation.transport.Propagator;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalException {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Bad Request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "404 Response",
                                    summary = "Handle exception when resource not found",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 404,
                                              "path": "/api/v1/...",
                                              "error": "Not Found",
                                              "message": "{data} not found"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setStatus(NOT_FOUND.value());
        errorResponse.setError(NOT_FOUND.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());

        return errorResponse;
    }

    @ExceptionHandler({ConstraintViolationException.class,
            MissingServletRequestParameterException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(BAD_REQUEST)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Handle exception when the data invalid. (@RequestBody, @RequestParam, @PathVariable)",
                                    summary = "Handle Bad Request",
                                    value = """
                                            {
                                                 "timestamp": "2024-04-07T11:38:56.368+00:00",
                                                 "status": 400,
                                                 "path": "/api/v1/...",
                                                 "error": "Invalid Payload",
                                                 "message": "{data} must be not blank"
                                             }
                                            """
                            ))})
    })
    public ErrorResponse handleValidationException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(BAD_REQUEST.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        String message = e.getMessage();
        if (e instanceof MethodArgumentNotValidException) {
            int start = message.lastIndexOf("[") + 1;
            int end = message.lastIndexOf("]") - 1;
            message = message.substring(start, end);
            errorResponse.setError("Invalid Payload");
            errorResponse.setMessage(message);
        } else if (e instanceof MissingServletRequestParameterException) {
            errorResponse.setError("Invalid Parameter");
            errorResponse.setMessage(message);
        } else if (e instanceof ConstraintViolationException) {
            errorResponse.setError("Invalid Parameter");
            errorResponse.setMessage(message.substring(message.indexOf(" ") + 1));
        } else {
            errorResponse.setError("Invalid Data");
            errorResponse.setMessage(message);
        }

        return errorResponse;
    }

    @ExceptionHandler(InstrumentModeChangeException.class)
    @ResponseStatus(BAD_REQUEST)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Lỗi Đổi Chế Độ Máy",
                                    summary = "Không thể đổi chế độ máy (ví dụ: đang chạy)",
                                    value = """
                                              {
                                                "timestamp": "2025-10-29T10:15:00.000+00:00",
                                                "status": 400,
                                                "path": "/api/v1/instrument/mode",
                                                "error": "Instrument Mode Change Failed",
                                                "message": "Cannot change mode while instrument is RUNNING"
                                              }
                                            """
                            ))})
    })
    public ErrorResponse handleInstrumentModeChangeException(InstrumentModeChangeException e, WebRequest request) {
        log.warn("[Business Exception]: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(BAD_REQUEST.value());
        errorResponse.setTimestamp(new Date());
        errorResponse.setError("Instrument Mode Change Failed");
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }

    @ExceptionHandler( InstrumentNotReadyException.class)
    @ResponseStatus(CONFLICT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(
                                            name = "Xung Đột Dữ Liệu",
                                            summary = "Dữ liệu đã tồn tại (ví dụ: trùng lặp)",
                                            value = """
                                                      {
                                                        "timestamp": "2025-10-29T10:20:00.000+00:00",
                                                        "status": 409,
                                                        "path": "/api/v1/resource/create",
                                                        "error": "Conflict",
                                                        "message": "Resource with this name already exists"
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Xung Đột Trạng Thái Máy",
                                            summary = "Máy không ở trạng thái hợp lệ để thực thi",
                                            value = """
                                                      {
                                                        "timestamp": "2025-10-29T10:21:00.000+00:00",
                                                        "status": 409,
                                                        "path": "/api/v1/blood-testing/start",
                                                        "error": "Conflict",
                                                        "message": "Instrument ID: 3 is not READY"
                                                      }
                                                    """
                                    )
                            })})
    })
    public ErrorResponse handleConflictException(RuntimeException e, WebRequest request) {
        log.warn("[Business Exception - Conflict]: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(CONFLICT.value());
        errorResponse.setTimestamp(new Date());
        errorResponse.setError(CONFLICT.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }
    @ExceptionHandler(WarehouseServiceException.class)
    @ResponseStatus(SERVICE_UNAVAILABLE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "503", description = "Service Unavailable",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Lỗi Dịch Vụ Ngoài",
                                    summary = "Khi một dịch vụ (microservice) khác bị lỗi",
                                    value = """
                                              {
                                                "timestamp": "2025-10-29T10:22:00.000+00:00",
                                                "status": 503,
                                                "path": "/api/v1/instrument/load-reagent",
                                                "error": "Service Unavailable",
                                                "message": "Warehouse Service is not responding"
                                              }
                                            """
                            ))})
    })
    public ErrorResponse handleWarehouseServiceException(WarehouseServiceException e, WebRequest request) {
        log.error("[Downstream Service Error]: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(SERVICE_UNAVAILABLE.value());
        errorResponse.setTimestamp(new Date());
        errorResponse.setError(SERVICE_UNAVAILABLE.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "500 Response",
                                    summary = "Handle exception when internal server error",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:35:52.333+00:00",
                                              "status": 500,
                                              "path": "/api/v1/...",
                                              "error": "Internal Server Error",
                                              "message": "Connection timeout, please try again"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setStatus(INTERNAL_SERVER_ERROR.value());
        errorResponse.setError(INTERNAL_SERVER_ERROR.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());

        return errorResponse;
    }



    @Getter
    @Setter
    private class ErrorResponse {
        private Date timestamp;
        private int status;
        private String path;
        private String error;
        private String message;
    }
}
