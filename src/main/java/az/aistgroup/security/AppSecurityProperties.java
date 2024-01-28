package az.aistgroup.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The security properties of Application.
 */
@ConfigurationProperties(prefix = "app.security")
public final class AppSecurityProperties {
    private static final TokenProperties tokenProperties = new TokenProperties();

    public static class TokenProperties {
        /**
         * Base64 secret key is used to sign JWT tokens.
         */
        private String base64Secret;

        /**
         * Indicates expiration time of access token in milliseconds.
         */
        private int accessTokenValidity;

        /**
         * Indicates expiration time of refresh token in milliseconds.
         */
        private int refreshTokenValidity;

        /**
         * The key is used inside request headers to store JWT Token.
         */
        private String authorizationHeaderText;

        /**
         * The prefix of JWT Tokens. e.g. "Bearer [token]"
         */
        private String tokenPrefix;

        public String getBase64Secret() {
            return base64Secret;
        }

        public void setBase64Secret(String base64Secret) {
            this.base64Secret = base64Secret;
        }

        public int getAccessTokenValidity() {
            return accessTokenValidity;
        }

        public void setAccessTokenValidity(int accessTokenValidity) {
            this.accessTokenValidity = accessTokenValidity;
        }

        public int getRefreshTokenValidity() {
            return refreshTokenValidity;
        }

        public void setRefreshTokenValidity(int refreshTokenValidity) {
            this.refreshTokenValidity = refreshTokenValidity;
        }

        public String getAuthorizationHeaderText() {
            return authorizationHeaderText;
        }

        public void setAuthorizationHeaderText(String authorizationHeaderText) {
            this.authorizationHeaderText = authorizationHeaderText;
        }

        public String getTokenPrefix() {
            return tokenPrefix;
        }

        public void setTokenPrefix(String tokenPrefix) {
            this.tokenPrefix = tokenPrefix;
        }
    }

    public TokenProperties getTokenProperties() {
        return tokenProperties;
    }
}
