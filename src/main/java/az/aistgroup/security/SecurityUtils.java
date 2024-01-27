package az.aistgroup.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * Used to provide helper methods for the security-related operations.
 */
public class SecurityUtils {

    /**
     * Checks whether logged-in user's username matches with the given username.
     *
     * @param username a username
     * @return returns {@code true} if currently logged-in user's username and given username are matched, otherwise {@code false}
     */
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

    /**
     * Checks whether user has permission to perform the operation.
     *
     * @param username a username
     * @throws AccessDeniedException if a user has no permission to perform operation.
     * @see #isSameLoggedInUser(String)
     */
    public static void checkUserHasPermission(String username) {
        if (SecurityUtils.isSameLoggedInUser(username)) {
            return;
        }

        throw new AccessDeniedException("You don't have permission to access the resource!");
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
