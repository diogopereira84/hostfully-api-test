package services;

import interfaces.IBookingService;
import io.restassured.response.Response;
import models.request.Booking;
import models.request.Guest;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

//Encapsulates property API calls, Follows Interface Segregation Principle (ISP)
public class BookingService implements IBookingService {

    private static final String BOOKING_ENDPOINT = "/bookings";


    @Override
    public Response bookingRetrievalAll() {
        return given()
                .get(BOOKING_ENDPOINT);
    }

    @Override
    public Response bookingCreation(Booking booking) {
        return given()
                .contentType(JSON)
                .body(booking)
                .post(BOOKING_ENDPOINT);
    }

    @Override
    public Response rebook(String bookingId, Booking booking) {
        return given()
                .contentType(JSON)
                .body(booking)
                .patch(BOOKING_ENDPOINT + "/" + bookingId + "/rebook");
    }

    @Override
    public Response updateGuest(String bookingId, Guest guest) {
        return given()
                .contentType(JSON)
                .body(guest)
                .patch(BOOKING_ENDPOINT + "/" + bookingId + "/guest");
    }

    @Override
    public Response bookingCancellation(String bookingId) {
        return given()
                .contentType(JSON)
                .patch(BOOKING_ENDPOINT + "/" + bookingId + "/cancel");
    }
}