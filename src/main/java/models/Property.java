package models;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Property {
    private String id;
    private String alias;
    private String countryCode; // Changing to String to accommodate any arbitrary string value or empty object.
    private Instant createdAt;
}
