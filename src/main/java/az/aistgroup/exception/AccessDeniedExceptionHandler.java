package az.aistgroup.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static az.aistgroup.exception.ErrorResponseCode.ACCESS_DENIED;

@Component
public class AccessDeniedExceptionHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        var errorResponse = ErrorResponse.getDefaultErrorResponse(HttpStatus.FORBIDDEN, ACCESS_DENIED,
                "You don't have permission to perform this operation!");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        AuthFailureHandler.handleResponse(response, errorResponse);
    }
}
