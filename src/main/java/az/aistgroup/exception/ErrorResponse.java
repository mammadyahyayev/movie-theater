package az.aistgroup.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private int status;
    private List<Error> errors;
    private LocalDateTime timestamp;

    ErrorResponse() {
        timestamp = LocalDateTime.now();
    }

    public static class Error {
        private final String code;
        private final String message;
        private final String field;

        public Error(String code, String message, String field) {
            this.code = code;
            this.message = message;
            this.field = field;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getField() {
            return field;
        }
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
