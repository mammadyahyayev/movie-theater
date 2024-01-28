package az.aistgroup.exception;

import az.aistgroup.domain.entity.User;

/**
 * The exception is thrown when {@link User} doesn't have enough money to buy something.
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
