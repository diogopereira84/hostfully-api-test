package helpers;

import models.request.Property;
import utils.DateTimeUtils;
import java.util.List;
import java.util.UUID;

public class PropertyHelper {

    public static Property constructProperty(List<String> fields) {
        Property.PropertyBuilder builder = Property.builder();

        String uuid = UUID.randomUUID().toString();
        String alias = "Property for Hostfully: " + uuid;
        String countryCode = "BR";
        String createdAt = DateTimeUtils.getCurrentUtcTimestamp();

        if (fields.contains("id")) {
            builder.id(uuid);
        }
        if (fields.contains("alias")) {
            builder.alias(alias);
        }
        if (fields.contains("countryCode")) {
            builder.countryCode(countryCode);
        }
        if (fields.contains("createdAt")) {
            builder.createdAt(createdAt);
        }

        return builder.build();
    }
}