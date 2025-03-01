package tests;

import base.BaseTest;
import io.restassured.response.Response;
import models.Booking;
import org.testng.Assert;
import org.testng.annotations.Test;
import requests.BookingRequests;

public class BookingTests extends BaseTest {

    @Test
    public void testCreateBooking() {
        Booking booking = Booking.builder()
                .propertyId("1")
                .startDate("2025-03-01")
                .endDate("2025-03-05")
                .build();

        Response response = BookingRequests.createBooking(booking);
        Assert.assertEquals(response.getStatusCode(), 201);
    }
}