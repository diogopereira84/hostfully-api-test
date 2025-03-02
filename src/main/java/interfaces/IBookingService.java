package interfaces;

import io.restassured.response.Response;
import models.request.Booking;
import models.request.Guest;

//Applies Dependency Inversion Principle (DIP), Allows flexible implementations
public interface IBookingService {
    Response bookingRetrievalAll();
    Response bookingCreation(Booking booking);
    Response rebook(String bookingId, Booking booking);
    Response updateGuest(String bookingId, Guest guest);
    Response bookingCancellation(String bookingId);
}