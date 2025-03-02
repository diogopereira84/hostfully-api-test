package models.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Property {
    private String id;
    private String alias;
    private String countryCode; // Changing to String to accommodate any arbitrary string value or empty object.
    private String createdAt;
}
