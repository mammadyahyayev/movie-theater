package az.aistgroup.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {
    private final TokenProperties tokenProperties = new TokenProperties();

    public static class TokenProperties {
        private String base64Secret;
        private int accessTokenValidity;
        private int refreshTokenValidity;
        private String authorizationHeaderText;
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
