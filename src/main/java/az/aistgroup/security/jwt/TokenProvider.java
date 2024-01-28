package az.aistgroup.security.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.Map;

public interface TokenProvider {
    String TOKEN_TYPE = "token_type";

    Map.Entry<String, Date> generateAccessToken(Authentication authentication);

    Map.Entry<String, Date> generateRefreshToken(Authentication authentication);

    Authentication getAuthentication(Claims claims);

    Claims getClaims(String token);

    JwtTokenProvider.TokenValidityResponse checkTokenValidity(String token);
}
