package az.aistgroup.exception;

/**
 * The exception will be thrown when there is already a resource
 * that cannot be added twice because it carries some unique parameters.
 */
public class ResourceAlreadyExistException extends RuntimeException {
    public ResourceAlreadyExistException(String message) {
        super(message);
    }
}
