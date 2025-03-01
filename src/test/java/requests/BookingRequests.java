package requests;

import io.restassured.response.Response;
import models.Booking;

import static io.restassured.RestAssured.given;

public class BookingRequests {

    public static Response createBooking(Booking booking) {
        return given()
                .header("Content-Type", "application/json")
                .body(booking)
                .post("/bookings");
    }
}