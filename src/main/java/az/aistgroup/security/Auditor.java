package az.aistgroup.security;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

/**
 * The class is used extract logged-in user from {@link SecurityContext} to use for
 * {@link CreatedBy} and {@link LastModifiedBy} annotations.
 * <p>
 * If there is not any logged-in user inside {@link SecurityContext}, then
 * <b>'anonymous'</b> string will be returned.
 * </p>
 */
public class Auditor implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Optional<Object> optionalAuditor = Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal);

        if (optionalAuditor.isEmpty()) return Optional.empty();

        Object auditor = optionalAuditor.get();

        if (auditor instanceof String) {
            return optionalAuditor.map(Object::toString);
        }

        if (auditor instanceof User) {
            return optionalAuditor
                    .map(User.class::cast)
                    .map(User::getUsername);
        }

        return Optional.empty();
    }
}
