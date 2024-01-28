package az.aistgroup.exception;

import az.aistgroup.domain.entity.Hall;

/**
 * The exception is thrown when {@code capacity} of {@link Hall} exceeded in updating or adding
 * operations.
 */
public class HallCapacityExceedException extends RuntimeException {
    public HallCapacityExceedException(String message) {
        super(message);
    }
}
