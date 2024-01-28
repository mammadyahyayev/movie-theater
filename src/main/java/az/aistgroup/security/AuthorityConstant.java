package az.aistgroup.security;

/**
 * The Authority constants are used to differentiate {@link az.aistgroup.domain.entity.User}s by their role.
 */
public final class AuthorityConstant {
    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    private AuthorityConstant() {
    }
}
