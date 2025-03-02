package models.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyResponse {
    private String id;
    private String alias;
    private String countryCode;
    private List<Integer> createdAt;
}
