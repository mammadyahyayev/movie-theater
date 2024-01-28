package az.aistgroup.exception;

/**
 * The class is used to simplify exception messages when throwing exception.
 */
public final class ExceptionMessageUtil {
    private ExceptionMessageUtil() {
    }

    public static String resourceNotFoundExceptionWithId(String entityName, Long id) {
        return entityName + " with id: " + id + " not found!";
    }
}
