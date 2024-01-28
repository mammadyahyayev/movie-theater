package az.aistgroup.security.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.Map;

/**
 * The interface provides the necessary methods to manage tokens such as
 * creation and validation.
 */
public interface TokenProvider {
    String TOKEN_TYPE = "token_type";

    /**
     * Generates access token.
     *
     * @param authentication an {@link Authentication}
     * @return {@link Map.Entry} with access token as the key, expiration date as the value.
     */
    Map.Entry<String, Date> generateAccessToken(Authentication authentication);

    /**
     * Generates refresh token.
     *
     * @param authentication an {@link Authentication}
     * @return {@link Map.Entry} with refresh token as the key, expiration date as the value.
     */
    Map.Entry<String, Date> generateRefreshToken(Authentication authentication);

    /**
     * Get {@link Authentication} which holds username and roles of {@link az.aistgroup.domain.entity.User}s
     *
     * @param claims a JWT {@link Claims}
     * @return {@link Authentication} which contains username and list of authorities.
     */
    Authentication getAuthentication(Claims claims);

    /**
     * Fetches JWT {@link Claims} of given token.
     *
     * @param token a JWT token
     * @return {@link Claims} of given token
     */
    Claims getClaims(String token);

    /**
     * Checks whether token is valid or not.
     *
     * <p>
     * It checks token expiration time and signature of JWT token. Token won't be valid
     * if it is expired or invalid (signature is different than expected). Returns successful
     * response if above problems aren't occurred.
     * </p>
     *
     * @param token a JWT token
     * @return {@link az.aistgroup.security.jwt.TokenValidityResponse} response
     */
    TokenValidityResponse checkTokenValidity(String token);
}
