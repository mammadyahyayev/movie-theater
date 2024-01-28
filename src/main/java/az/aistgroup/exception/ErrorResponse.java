package az.aistgroup.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The base class is used for returning Error response.
 */
public class ErrorResponse {
    private int status;
    private List<Error> errors;
    private LocalDateTime timestamp;

    ErrorResponse() {
        timestamp = LocalDateTime.now();
    }

    public record Error(String code, String message, String field) {
    }

    public static ErrorResponse getDefaultErrorResponse(HttpStatus status, ErrorResponseCode response, String message) {
        return getDefaultErrorResponse(status, response, message, null);
    }

    /**
     * The method is used specifically for Validation errors to define {@code field}
     * which validation failed.
     *
     * @param status   an {@link HttpStatus}
     * @param response a custom {@link ErrorResponseCode}
     * @param message  a message of the error
     * @param field    a field that validation failed.
     * @return an {@link ErrorResponse}
     */
    public static ErrorResponse getDefaultErrorResponse(HttpStatus status, ErrorResponseCode response, String message, String field) {
        var errorResponse = new ErrorResponse();
        errorResponse.setStatus(status.value());
        errorResponse.setErrors(List.of(new ErrorResponse.Error(response.getCode(), message, field)));
        return errorResponse;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
