package az.aistgroup.security.jwt;

import org.springframework.security.core.Authentication;

public interface TokenGenerator {
    String generateAccessToken(Authentication authentication);

    String generateRefreshToken(Authentication authentication);

    Authentication getAuthentication(String token);

    boolean isValidToken(String token);
}
