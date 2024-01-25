package az.aistgroup.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UnAuthorizedExceptionHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        var errorResponse = ErrorResponse.getDefaultErrorResponse(HttpStatus.UNAUTHORIZED,
                ErrorResponseCode.UNAUTHORIZED, "You aren't authorized to send request!");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        AuthFailureHandler.handleResponse(response, errorResponse);
    }
}
