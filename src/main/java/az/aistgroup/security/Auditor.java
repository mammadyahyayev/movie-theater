package az.aistgroup.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import java.util.Optional;

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
