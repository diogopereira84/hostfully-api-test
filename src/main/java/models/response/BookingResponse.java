package models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.request.Guest;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private String id;
    private List<Integer> startDate; // Format: [YYYY, MM, DD]
    private List<Integer> endDate;   // Format: [YYYY, MM, DD]
    private String status;           // Example: "SCHEDULED"
    private GuestResponse guest;
    private String propertyId;
}
