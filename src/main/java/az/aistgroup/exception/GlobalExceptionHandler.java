package az.aistgroup.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.text.MessageFormat;
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
                handleFieldErrors(err, fieldError, errors);
            }
        }

        errorResponse.setErrors(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private void handleFieldErrors(ObjectError err, FieldError fieldError, List<ErrorResponse.Error> errors) {
        var violation = err.unwrap(ConstraintViolation.class);
        String messageTemplate = violation.getMessageTemplate();
        String message = fieldError.getDefaultMessage();
        String field = fieldError.getField();
        if (messageTemplate.contains("field") && message != null) {
            message = MessageFormat.format(message, field);
        }
        errors.add(new ErrorResponse.Error("validation.error", message, field));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND, e.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CapacityExceedException.class)
    public ResponseEntity<ErrorResponse> handleCapacityExceedException(CapacityExceedException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.BAD_REQUEST, CAPACITY_EXCEEDED, e.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistException(ResourceAlreadyExistException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.BAD_REQUEST, RESOURCE_ALREADY_EXIST, e.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.BAD_REQUEST, INVALID_REQUEST, e.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.UNAUTHORIZED, BAD_CREDENTIALS, e.getMessage(), null);
        log.error("Bad Credentials Exception: {}", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest req) {
        var errorResponse = ErrorResponse.getDefaultErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED,
                "You don't have permission to perform this operation!");
        log.error("Access Denied Exception: {}", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e, HttpServletRequest req) {
        var errorResponse = ErrorResponse.getDefaultErrorResponse(HttpStatus.UNAUTHORIZED,
                ErrorResponseCode.UNAUTHORIZED, "You aren't authorized to send request!");
        log.error("Authentication Exception: {}", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
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
