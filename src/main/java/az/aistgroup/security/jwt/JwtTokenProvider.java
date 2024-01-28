package az.aistgroup.security.jwt;

import az.aistgroup.exception.ErrorResponseCode;
import az.aistgroup.security.AppSecurityProperties;
import az.aistgroup.security.TokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The class is used to manage JWT tokens.
 */
@Primary
@Component
public class JwtTokenProvider implements TokenProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);


    private static final String ISSUER = "AISTGroup MMC";
    private static final String AUDIENCE = "Movie Theater";
    private static final String AUTHORITIES_KEY = "auth";
    private static final String AUTHORITIES_DELIMITER = ",";

    private final JwtParser jwtParser;
    private final Key key;
    private final AppSecurityProperties appSecurityProperties;

    public JwtTokenProvider(AppSecurityProperties appSecurityProperties) {
        this.appSecurityProperties = appSecurityProperties;
        this.key = Keys.hmacShaKeyFor(appSecurityProperties.getTokenProperties().getBase64Secret().getBytes());
        this.jwtParser = Jwts.parserBuilder()
                .requireIssuer(ISSUER)
                .requireAudience(AUDIENCE)
                .setSigningKey(this.key)
                .build();
    }

    @Override
    public Map.Entry<String, Date> generateAccessToken(Authentication authentication) {
        var tokenProperties = appSecurityProperties.getTokenProperties();
        long now = (new Date()).getTime();
        var validity = new Date(now + Duration.ofSeconds(tokenProperties.getAccessTokenValidity()).toMillis());
        return generateToken(authentication, validity, TokenType.ACCESS_TOKEN);
    }

    @Override
    public Map.Entry<String, Date> generateRefreshToken(Authentication authentication) {
        var tokenProperties = appSecurityProperties.getTokenProperties();
        long now = (new Date()).getTime();
        long ms = now + Duration.ofSeconds(tokenProperties.getRefreshTokenValidity()).toMillis();
        return generateToken(authentication, new Date(ms), TokenType.REFRESH_TOKEN);
    }

    private Map.Entry<String, Date> generateToken(Authentication authentication, Date validity, TokenType tokenType) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(AUTHORITIES_DELIMITER));

        String token = Jwts.builder()
                .setIssuer(ISSUER)
                .setAudience(AUDIENCE)
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(TOKEN_TYPE, tokenType)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .setIssuedAt(new Date())
                .compact();

        return new AbstractMap.SimpleEntry<>(token, validity);
    }

    @Override
    public Authentication getAuthentication(Claims claims) {
        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get(AUTHORITIES_KEY).toString().split(AUTHORITIES_DELIMITER))
                .filter(auth -> !auth.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    public Claims getClaims(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            log.error("JWT Token expired: {}, {}", e, e.getMessage());
        } catch (UnsupportedJwtException | MalformedJwtException | SecurityException e) {
            log.error("Invalid JWT Token: {}, {}", e, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Token validation error {}", e.getMessage());
        }

        return null;
    }

    public TokenValidityResponse checkTokenValidity(String token) {
        try {
            jwtParser.parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            log.error("JWT Token expired: {}, {}", e, e.getMessage());
            return new TokenValidityResponse(false, ErrorResponseCode.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException | MalformedJwtException | SecurityException | MissingClaimException |
                 IllegalArgumentException e) {
            log.error("Invalid JWT Token: {}, {}", e, e.getMessage());
            return new TokenValidityResponse(false, ErrorResponseCode.INVALID_TOKEN);
        }

        return new TokenValidityResponse(true, null);
    }


}
