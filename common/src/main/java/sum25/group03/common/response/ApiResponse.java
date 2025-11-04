package sum25.group03.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final Integer status;
    private final String error;
    private final String message;
    private final String path;
    @Builder.Default
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final T data;

    public static <T> ApiResponseBuilder<T> status(HttpStatus status) {
        return ApiResponse.<T>builder().status(status.value());
    }

    public static <T> ApiResponseBuilder<T> status(int status) {
        return ApiResponse.<T>builder().status(status);
    }
    public static <T> ApiResponseBuilder<T> error(String error) {
        return ApiResponse.<T>builder().message(error);
    }
    public static <T> ApiResponseBuilder<T> path(String path) {
        return ApiResponse.<T>builder().message(path);
    }

    public static <T> ApiResponse<T> add(String message, T data) {
        return ApiResponse.<T>builder().message(message).data(data).build();
    }

    public static <T> ApiResponseBuilder<T> message(String message) {
        return ApiResponse.<T>builder().message(message);
    }

    public static <T> ApiResponseBuilder<T> data(T data) {
        return ApiResponse.<T>builder().data(data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.CREATED.value())
                .message("Created successfully")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message, String path) {
        return ApiResponse.<T>builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }
}
