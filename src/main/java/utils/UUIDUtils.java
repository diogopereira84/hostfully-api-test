package utils;

import java.util.UUID;

public class UUIDUtils {

    /**
     * Validates if a given string is a valid UUID format.
     *
     * @param uuid The string to validate.
     * @return true if the string is a valid UUID, false otherwise.
     */
    public static boolean isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Generates a random UUID as a string.
     *
     * @return A new UUID in string format.
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    // Main method for quick testing
    public static void main(String[] args) {
        String validUuid = UUIDUtils.generateUUID();
        String invalidUuid = "invalid-uuid-string";

        System.out.println("Generated UUID: " + validUuid);
        System.out.println("Is valid? " + UUIDUtils.isValidUUID(validUuid));
        System.out.println("Is valid? " + UUIDUtils.isValidUUID(invalidUuid));
    }
}