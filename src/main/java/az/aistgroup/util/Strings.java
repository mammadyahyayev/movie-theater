package az.aistgroup.util;

/**
 * Utility class for Strings
 */
public final class Strings {

    private Strings() {
    }

    /**
     * Checks whether a {@link String} is empty or not.
     *
     * @param str a {@link String} object
     * @return {@code true} if {@link String} is empty or {@code null},
     * otherwise {@code false}
     */
    public static boolean isNullOrEmpty(final String str) {
        return str == null || str.isBlank();
    }

    /**
     * Checks whether a {@link String} has text or not.
     *
     * @param str a {@link String} object
     * @return {@code true} if {@code String} has text and doesn't contain
     * spaces, otherwise false
     */
    public static boolean hasText(final String str) {
        return str != null && !str.trim().isEmpty() && !str.isBlank();
    }

}
