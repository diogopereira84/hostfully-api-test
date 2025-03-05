package helpers;

import models.request.Booking;
import models.request.Guest;
import models.response.BookingResponse;
import services.BookingService;
import utils.DateTimeUtils;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

import java.util.List;
import java.util.UUID;

/**
 * Helper class for Booking-related operations.
 */
public class BookingHelper {

    private static final BookingService bookingService = new BookingService();

    /**
     * Creates a valid Booking object with dynamic dates and a random UUID.
     *
     * @param propertyId The property ID for the booking.
     * @return A valid Booking object.
     */
    public static Booking createValidBooking(String propertyId) {
        String lastBookedDate = getLastBookedDate(propertyId);
        String endDate = DateTimeUtils.addDays(lastBookedDate, 1);

        return Booking.builder()
                .id(UUID.randomUUID().toString())
                .startDate(lastBookedDate)
                .endDate(endDate)
                .status("SCHEDULED")
                .guest(new Guest("Diogo", "Pereira", "1984-05-18"))
                .propertyId(propertyId)
                .build();
    }

    /**
     * Creates a new valid booking for the given property ID.
     *
     * @param propertyId The property ID for the booking.
     * @return A Booking object with auto-generated data.
     */
    public static Booking createBookingUsingAvailableDates(String propertyId) {
        String lastBookedDate = getLastBookedDate(propertyId);
        String endDate = DateTimeUtils.addDays(lastBookedDate, 1);

        return Booking.builder()
                .id(UUID.randomUUID().toString())
                .startDate(lastBookedDate)
                .endDate(endDate)
                .status("SCHEDULED")
                .guest(new Guest("Diogo", "Pereira", "1984-05-18"))
                .propertyId(propertyId)
                .build();
    }
    /**
     * Creates a Booking with custom fields.
     *
     * @param id         The booking ID.
     * @param startDate  Start date.
     * @param endDate    End date.
     * @param propertyId Property ID.
     * @param status     Booking status.
     * @param guest      Guest details.
     * @return A Booking object with specified fields.
     */
    public static Booking createBooking(String id, String startDate, String endDate, String propertyId, String status, Guest guest) {
        return Booking.builder()
                .id(id)
                .startDate(startDate)
                .endDate(endDate)
                .status(status)
                .guest(guest)
                .propertyId(propertyId)
                .build();
    }

    /**
     * Retrieves the latest booked end date for a given property ID.
     *
     * @param propertyId The property ID to check.
     * @return The latest booked end date (YYYY-MM-DD) or a default value.
     */
    public static String getLastBookedDate(String propertyId) {
        Response response = bookingService.bookingRetrievalAll();
        List<BookingResponse> bookings = response.as(new TypeRef<>() {});

        return bookings.stream()
                .filter(b -> b.getPropertyId().equals(propertyId))
                .map(b -> DateTimeUtils.formatDate(b.getEndDate()))
                .reduce((first, second) -> second) // Get last occurrence
                .orElse(DateTimeUtils.getCurrentUtcTimestamp()); // Default to current date if no bookings exist
    }

    public static Booking createCustomBooking(String propertyId, String startDate, String endDate) {

        return Booking.builder()
                .id(UUID.randomUUID().toString())
                .startDate(startDate)
                .endDate(endDate)
                .status("SCHEDULED")
                .guest(new Guest("Diogo", "Pereira", "1984-05-18"))
                .propertyId(propertyId)
                .build();

    }
}