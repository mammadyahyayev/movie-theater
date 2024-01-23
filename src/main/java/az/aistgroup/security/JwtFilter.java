package az.aistgroup.security;

import az.aistgroup.security.jwt.TokenGenerator;
import az.aistgroup.util.Strings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final TokenGenerator tokenGenerator;
    private final AppSecurityProperties securityProperties;

    public JwtFilter(
            TokenGenerator tokenGenerator,
            AppSecurityProperties securityProperties
    ) {
        this.tokenGenerator = tokenGenerator;
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = this.resolveToken(request);
        if (Strings.hasText(token) && tokenGenerator.isValidToken(token)) {
            Authentication authentication = tokenGenerator.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Returns token without 'Bearer' prefix if token starts with prefix,
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
