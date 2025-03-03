package helpers;

import models.request.Property;
import utils.DateTimeUtils;
import utils.UUIDUtils;

import java.util.List;
import java.util.UUID;

/**
 * Helper class for constructing Property objects with various configurations.
 */
public class PropertyHelper {

    /**
     * Creates a valid Property object with random UUID, alias, and current UTC timestamp.
     *
     * @return A valid Property object
     */
    public static Property createValidProperty() {
        String uuid = UUIDUtils.generateUUID();
        String alias = "Property for Hostfully: " + uuid;
        String countryCode = "BR";
        String createdAt = DateTimeUtils.getCurrentUtcTimestamp();

        return Property.builder()
                .id(uuid)
                .alias(alias)
                .countryCode(countryCode)
                .createdAt(createdAt)
                .build();
    }

    /**
     * Creates a Property object
     *
     * @return A Property object
     */
    public static Property createProperty(String id, String alias, String countryCode, String createdAt) {
        return Property.builder()
                .id(id.isEmpty() ? null : id)
                .alias(alias.isEmpty() ? null : alias)
                .countryCode(countryCode.isEmpty() ? null : countryCode)
                .createdAt(createdAt.isEmpty() ? null : createdAt)
                .build();
    }

    /**
     * Constructs a Property object based on provided fields.
     * - If a field is not in the list, it is set to null (simulating missing fields).
     *
     * @param fields List of fields to include in the Property object.
     * @return A Property object with the specified fields.
     */
    public static Property constructProperty(List<String> fields) {
        String uuid = fields.contains("id") ? UUIDUtils.generateUUID() : null;
        String alias = fields.contains("alias") ? "Property for Hostfully" : null;
        String countryCode = fields.contains("countryCode") ? "BR" : null;
        String createdAt = fields.contains("createdAt") ? DateTimeUtils.getCurrentUtcTimestamp() : null;

        return Property.builder()
                .id(uuid)
                .alias(alias)
                .countryCode(countryCode)
                .createdAt(createdAt)
                .build();
    }
}