package az.aistgroup.exception;

import az.aistgroup.security.TokenType;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static az.aistgroup.exception.ErrorResponse.getDefaultErrorResponse;
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
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
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
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
    }

    @ExceptionHandler(CapacityExceedException.class)
    public ResponseEntity<ErrorResponse> handleCapacityExceedException(CapacityExceedException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.BAD_REQUEST, CAPACITY_EXCEEDED, e.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
    }

    @ExceptionHandler(SeatAlreadyBookedException.class)
    public ResponseEntity<ErrorResponse> handleSeatAlreadyBookedException(SeatAlreadyBookedException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.BAD_REQUEST, SEAT_BOOKED, e.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(InsufficientFundsException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.BAD_REQUEST, INSUFFICIENT_FUNDS, e.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
    }

    @ExceptionHandler(NoTicketsAvailableException.class)
    public ResponseEntity<ErrorResponse> handleNoTicketsAvailableException(NoTicketsAvailableException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.BAD_REQUEST, NO_TICKETS_AVAILABLE, e.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistException(ResourceAlreadyExistException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.BAD_REQUEST, RESOURCE_ALREADY_EXIST, e.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.BAD_REQUEST, INVALID_REQUEST, e.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
    }

    @ExceptionHandler(TicketExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTicketExpiredException(TicketExpiredException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.BAD_REQUEST, TICKET_EXPIRED, e.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.UNAUTHORIZED, BAD_CREDENTIALS, e.getMessage(), null);
        log.error("Bad Credentials Exception: {}", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED,
                "You don't have permission to perform this operation!");
        log.error("Access Denied Exception: {}", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e, HttpServletRequest req) {
        var errorResponse = getDefaultErrorResponse(HttpStatus.UNAUTHORIZED,
                ErrorResponseCode.UNAUTHORIZED, "You aren't authorized to send request!");
        log.error("Authentication Exception: {}", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
    }

    @ExceptionHandler(TokenValidityException.class)
    public ResponseEntity<ErrorResponse> handleTokenValidityException(TokenValidityException e, HttpServletRequest req) {
        ErrorResponseCode code;
        String message;

        if (e.getMessage().contains(TOKEN_EXPIRED.getCode())) {
            if (e.getTokenType() == TokenType.REFRESH_TOKEN) {
                code = REFRESH_TOKEN_EXPIRED;
                message = "Refresh token is already expired, please login to get new token combination";
            } else {
                code = TOKEN_EXPIRED;
                message = "Access token is already expired, please use refresh token to get new access token!";
            }
        } else if (e.getMessage().contains(INVALID_TOKEN.getCode())) {
            code = INVALID_TOKEN;
            message = "Token is invalid, please use correct token!";
        } else {
            code = ACCESS_DENIED;
            message = e.getMessage();
        }

        var errorResponse = getDefaultErrorResponse(HttpStatus.FORBIDDEN, code, message);
        log.error("Token Validity Exception: {}", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e, HttpServletRequest request) {
        var errorResponse = getDefaultErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR,
                "Internal Server Error happened. We are working on it...");

        log.error("Internal Exception happened: {}", e.getMessage(), e);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
    }
}
