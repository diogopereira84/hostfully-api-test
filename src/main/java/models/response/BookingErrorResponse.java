package models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingErrorResponse {
    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;

    @JsonProperty("BOOKING_DATES_UNAVAILABLE")
    private String bookingDatesUnavailable; // Mapping JSON uppercase field to camelCase
}