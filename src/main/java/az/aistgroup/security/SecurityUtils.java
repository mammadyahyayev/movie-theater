package az.aistgroup.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtils {
    public static boolean isSameLoggedInUser(final String username) {
        return SecurityUtils.getCurrentLoggedInUser().filter(u -> u.equals(username)).isPresent();
    }

    /**
     * Get currently a logged-in Spring Security user.
     *
     * @return returns currently logged-in user's username
     */
    public static Optional<String> getCurrentLoggedInUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(context.getAuthentication()));
    }

    private static String extractPrincipal(final Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }


    private SecurityUtils() {
    }
}
