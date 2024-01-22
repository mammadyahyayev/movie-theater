package az.aistgroup.security.jwt;

import org.springframework.security.core.Authentication;

public interface TokenGenerator {
    String generateToken(Authentication authentication, boolean rememberMe);

    Authentication getAuthentication(String token);

    boolean isValidToken(String token);
}
