package models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    private String id;
    private String startDate;
    private String endDate;
    private String status;
    private Guest guest;
    private String propertyId;
}