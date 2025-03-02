package tests;

import base.BaseTest;
import helpers.AssertionHelper;
import helpers.PropertyHelper;
import io.restassured.response.Response;
import models.request.Booking;
import models.request.Guest;
import models.response.BookingResponse;
import models.response.PropertyResponse;
import models.response.ValidationErrorDetail;
import models.response.ValidationErrorResponse;
import models.response.GuestResponse;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import services.AuthRole;
import services.AuthenticationService;
import services.BookingService;
import utils.DateTimeUtils;
import utils.UUIDUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Listeners(utils.TestListener.class)
public class BookingTests extends BaseTest {

    private SoftAssert softAssert; // Declare SoftAssert globally for each test

    /**
     * Initializes SoftAssert before each test.
     */
    private void initSoftAssert() {
        softAssert = new SoftAssert();
    }

    @Test(groups = {"positive", "regression"})
    public void shouldReturn200WhenGettingBookingRetrievalAllForTheFirstBookedEntryTest() {
        initSoftAssert();

        Response response = bookingService.bookingRetrievalAll();
        List<BookingResponse> bookingResponses = response.as(new io.restassured.common.mapper.TypeRef<List<BookingResponse>>() {});

        // Validate response status
        softAssert.assertEquals(response.getStatusCode(), 200, "Expected 200 OK");
        softAssert.assertFalse(bookingResponses.isEmpty(), "Booking response list should not be empty");

        // Validate booking details
        BookingResponse expectedBooking = BookingResponse.builder()
                .id("ef7dc5f7-210a-45e2-afab-d5b72c7bcf0b")
                .startDate(Arrays.asList(2025, 3, 1))
                .endDate(Arrays.asList(2025, 3, 1))
                .status("SCHEDULED")
                .guest(GuestResponse.builder()
                        .firstName("Diogo")
                        .lastName("Pereira")
                        .dateOfBirth(Arrays.asList(2025, 3, 1))
                        .build())
                .propertyId("201c3466-153a-403b-9434-e3ff413a84cc")
                .build();

        BookingResponse actualBooking = bookingResponses.stream()
                .filter(b -> b.getId().equals(expectedBooking.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected booking not found!"));

        // Assertions for each field
        softAssert.assertEquals(actualBooking.getId(), expectedBooking.getId(), "Booking ID mismatch");
        softAssert.assertEquals(actualBooking.getStartDate(), expectedBooking.getStartDate(), "Start date mismatch");
        softAssert.assertEquals(actualBooking.getEndDate(), expectedBooking.getEndDate(), "End date mismatch");
        softAssert.assertEquals(actualBooking.getStatus(), expectedBooking.getStatus(), "Status mismatch");
        softAssert.assertEquals(actualBooking.getGuest().getFirstName(), expectedBooking.getGuest().getFirstName(), "Guest first name mismatch");
        softAssert.assertEquals(actualBooking.getGuest().getLastName(), expectedBooking.getGuest().getLastName(), "Guest last name mismatch");
        softAssert.assertEquals(actualBooking.getGuest().getDateOfBirth(), expectedBooking.getGuest().getDateOfBirth(), "Guest date of birth mismatch");
        softAssert.assertEquals(actualBooking.getPropertyId(), expectedBooking.getPropertyId(), "Property ID mismatch");

        softAssert.assertAll();
    }

    @Test(groups = {"positive", "regression"})
    public void shouldReturn200WhenGettingBookingRetrievalAllTest() {
        initSoftAssert();

        Response response = bookingService.bookingRetrievalAll();
        List<BookingResponse> bookingResponses = response.as(new io.restassured.common.mapper.TypeRef<List<BookingResponse>>() {});

        // Extract IDs and PropertyIds
        List<String> ids = bookingResponses.stream().map(BookingResponse::getId).toList();
        List<String> aliases = bookingResponses.stream().map(BookingResponse::getPropertyId).toList();

        // Validate uniqueness
        softAssert.assertEquals(new HashSet<>(ids).size(), ids.size(), "Duplicate IDs found!");
        softAssert.assertEquals(new HashSet<>(aliases).size(), aliases.size(), "Duplicate PropertiesId found!");
        softAssert.assertTrue(!bookingResponses.isEmpty());
        softAssert.assertEquals(response.getStatusCode(), 200, "Expected 200 OK");
        softAssert.assertAll();
    }

    @Test(groups = {"negative", "security", "regression"})
    public void shouldReturn401WhenGettingBookingRetrievalAllWithInvalidCredentialsTest() {
        initSoftAssert();

        AuthenticationService.setInvalidAuth();
        Response response = bookingService.bookingRetrievalAll();

        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);

        softAssert.assertEquals(response.getStatusCode(), 401, "Expected 401 Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getException(), "Bad credentials");
        softAssert.assertEquals(validationErrorResponse.getPath(), "/bookings");
        softAssert.assertEquals(validationErrorResponse.getError(), "Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getMessage(), "Error while authenticating your access");
        softAssert.assertNotNull(validationErrorResponse.getTimestamp());
        softAssert.assertAll();
    }

    @Test(groups = {"negative", "security", "regression"})
    public void shouldReturn401WhenGettingBookingRetrievalAllWithNoCredentialsTest() {
        initSoftAssert();

        AuthenticationService.removeAuth();
        Response response = bookingService.bookingRetrievalAll();

        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);

        softAssert.assertEquals(response.getStatusCode(), 401, "Expected 401 Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getException(), "Full authentication is required to access this resource");
        softAssert.assertEquals(validationErrorResponse.getPath(), "/bookings");
        softAssert.assertEquals(validationErrorResponse.getError(), "Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getMessage(), "Error while authenticating your access");
        softAssert.assertNotNull(validationErrorResponse.getTimestamp());
        softAssert.assertAll();
    }

    @Test(groups = {"negative", "security", "regression"})
    public void shouldReturn403WhenUserWithoutPermissionTriesToRetrieveBookingsTest(){
        initSoftAssert();

        AuthenticationService.setAuthRole(AuthRole.USER_ROLE);
        Response response = bookingService.bookingRetrievalAll();

        softAssert.assertEquals(response.getStatusCode(), 403, "Expected 403 Forbidden");
        softAssert.assertAll();
    }

    @Test(groups = {"positive", "regression"})
    public void shouldReturn200WhenGettingBookingCreationTest() {
        initSoftAssert();
        String newUUID = UUID.randomUUID().toString();

        // Validate booking details
        Booking book = Booking.builder()
                .id(newUUID)
                .startDate("2025-05-01")
                .endDate("2025-06-01")
                .status("SCHEDULED")
                .guest(Guest.builder()
                        .firstName("Diogo")
                        .lastName("Pereira")
                        .dateOfBirth("2025-03-01")
                        .build())
                .propertyId("201c3466-153a-403b-9434-e3ff413a84cc")
                .build();

        Response response = bookingService.bookingCreation(book);
        List<BookingResponse> bookingResponses = response.as(new io.restassured.common.mapper.TypeRef<List<BookingResponse>>() {});

        // Validate response status
        softAssert.assertEquals(response.getStatusCode(), 200, "Expected 200 OK");
        softAssert.assertFalse(bookingResponses.isEmpty(), "Booking response list should not be empty");
    }
}