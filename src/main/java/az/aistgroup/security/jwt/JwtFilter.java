package az.aistgroup.security.jwt;

import az.aistgroup.security.AppSecurityProperties;
import az.aistgroup.util.Strings;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * The JWT Filter is used to extract token from request headers and check its validity.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final AppSecurityProperties securityProperties;

    public JwtFilter(
            TokenProvider tokenProvider,
            AppSecurityProperties securityProperties
    ) {
        this.tokenProvider = tokenProvider;
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = this.resolveToken(request);

        if (Strings.hasText(token) && tokenProvider.checkTokenValidity(token).isValid()) {
            Claims claims = tokenProvider.getClaims(token);
            Authentication authentication = tokenProvider.getAuthentication(claims);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Returns token without <b>'Bearer'</b> prefix if token starts with prefix,
     * otherwise {@code null}
     *
     * @param request HTTP request
     * @return token without 'Bearer' prefix
     */
    private String resolveToken(HttpServletRequest request) {
        var tokenProperties = securityProperties.getTokenProperties();
        String bearerToken = request.getHeader(tokenProperties.getAuthorizationHeaderText());
        String tokenPrefix = tokenProperties.getTokenPrefix();

        if (Strings.hasText(bearerToken) && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length());
        }

        return null;
    }
}
