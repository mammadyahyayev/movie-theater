package az.aistgroup.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private HttpStatus status;
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

    public static ErrorResponse getDefaultErrorResponse(HttpStatus status, ErrorResponseCode response, String message, String field) {
        var errorResponse = new ErrorResponse();
        errorResponse.setStatus(status);
        errorResponse.setErrors(List.of(new ErrorResponse.Error(response.getCode(), message, field)));
        return errorResponse;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
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
