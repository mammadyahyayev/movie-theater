package az.aistgroup.exception;

import az.aistgroup.security.TokenType;

/**
 * The exception will be thrown when <b>JWT token</b> is expired or invalid.
 */
public class TokenValidityException extends RuntimeException {
    private final TokenType tokenType;

    public TokenValidityException(TokenType tokenType, ErrorResponseCode code) {
        this(tokenType, code.getCode());
    }

    public TokenValidityException(TokenType tokenType, String message) {
        super(message);
        this.tokenType = tokenType;
    }

    public TokenType getTokenType() {
        return tokenType;
    }
}
