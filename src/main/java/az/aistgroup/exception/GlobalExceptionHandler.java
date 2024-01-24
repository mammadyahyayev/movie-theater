package az.aistgroup.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import static az.aistgroup.exception.ErrorResponseCode.*;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request
    ) {
        var errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());

        var errors = new ArrayList<ErrorResponse.Error>();
        for (ObjectError err : ex.getBindingResult().getAllErrors()) {
            if (err instanceof FieldError fieldError) {
                String field = fieldError.getField();
                String message = fieldError.getDefaultMessage();
                errors.add(new ErrorResponse.Error("validation.error", message, field));
            }
        }

        errorResponse.setErrors(errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND, e.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistException(ResourceAlreadyExistException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.BAD_REQUEST, RESOURCE_ALREADY_EXIST, e.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.UNAUTHORIZED, BAD_CREDENTIALS, e.getMessage(), null);
        log.error("Bad Credentials Exception: {}", e.getMessage(), e);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED,
                "You don't have permission to perform this operation!", null);

        log.error("Access Denied: {}", e.getMessage(), e);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e, HttpServletRequest request) {
        var errorResponse = getDefaultErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR,
                "Internal Server Error happened. We are working on it...", null
        );

        log.error("Internal Exception happened: {}", e.getMessage(), e);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse getDefaultErrorResponse(HttpStatus status, ErrorResponseCode response, String message, String field) {
        var errorResponse = new ErrorResponse();
        errorResponse.setStatus(status.value());
        errorResponse.setErrors(List.of(new ErrorResponse.Error(response.getCode(), message, field)));
        return errorResponse;
    }
}
