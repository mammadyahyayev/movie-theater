package az.aistgroup.exception;

import az.aistgroup.security.TokenType;

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
