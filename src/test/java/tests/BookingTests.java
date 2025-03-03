package tests;

import base.BaseTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import models.request.Booking;
import models.request.Guest;
import models.response.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import services.AuthRole;
import services.AuthenticationService;
import utils.DateTimeUtils;

import java.util.*;

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
    public void shouldReturn201WhenGettingBookingCreationTest() {
        initSoftAssert();
        String newUUID = UUID.randomUUID().toString();
        String targetPropertyId = "e38698fc-2944-4c1a-9cc2-afbd5993d2ed";

        String lastBookedDate = getLastBookedDate(targetPropertyId);
        String endDate = DateTimeUtils.addOneDay(lastBookedDate);

        // Validate booking details
        Booking book = Booking.builder()
                .id(newUUID)
                .startDate(lastBookedDate)
                .endDate(endDate)
                .status("SCHEDULED")
                .guest(Guest.builder()
                        .firstName("Diogo")
                        .lastName("Pereira")
                        .dateOfBirth("1984-05-18")
                        .build())
                .propertyId(targetPropertyId)
                .build();

        Response response = bookingService.bookingCreation(book);
        BookingResponse bookingResponse = response.as(BookingResponse.class);

        String expectedStartDate = DateTimeUtils.formatDate(bookingResponse.getStartDate());
        String expectedEndDate = DateTimeUtils.formatDate(bookingResponse.getEndDate());
        String expectedDateOfBirth = DateTimeUtils.formatDate(bookingResponse.getGuest().getDateOfBirth());

        // Assert HTTP status code
        softAssert.assertEquals(response.getStatusCode(), 201, "Expected 201 Created");

        // Validate JSON Response Fields
        softAssert.assertNotNull(bookingResponse.getId(), "Booking ID should not be null");
        softAssert.assertEquals(expectedStartDate, lastBookedDate, "Start date should match");
        softAssert.assertEquals(expectedEndDate, endDate, "End date should match");
        softAssert.assertEquals(bookingResponse.getStatus(), "SCHEDULED", "Status should be SCHEDULED");

        // Validate Guest Details
        softAssert.assertNotNull(bookingResponse.getGuest(), "Guest object should not be null");
        softAssert.assertEquals(bookingResponse.getGuest().getFirstName(), "Diogo", "First name should match");
        softAssert.assertEquals(bookingResponse.getGuest().getLastName(), "Pereira", "Last name should match");
        softAssert.assertEquals(expectedDateOfBirth, "1984-05-18", "Date of birth should match");

        // Validate Property ID
        softAssert.assertEquals(bookingResponse.getPropertyId(), targetPropertyId, "Property ID should match");

        // Assert all to ensure all validations are executed
        softAssert.assertAll();
    }

    /**
     * DataProvider for all negative test cases with missing mandatory fields while including all optional fields.
     */
    @DataProvider(name = "missingMandatoryWithAllOptional")
    public Object[][] provideMissingMandatoryFieldsWithOptional() {
        String id = UUID.randomUUID().toString();
        String status = "SCHEDULED";
        Guest guest = new Guest("Diogo", "Pereira", "1984-05-18");

        return new Object[][]{
                {null, "2025-07-01", "e38698fc-2944-4c1a-9cc2-afbd5993d2ed", id, status, guest},
                {"2025-06-01", null, "e38698fc-2944-4c1a-9cc2-afbd5993d2ed", id, status, guest},
                {"2025-06-01", "2025-07-01", null, id, status, guest},
                {null, null, "e38698fc-2944-4c1a-9cc2-afbd5993d2ed", id, status, guest},
                {null, "2025-07-01", null, id, status, guest},
                {"2025-06-01", null, null, id, status, guest},
                {null, null, null, id, status, guest}
        };
    }

    /**
     * Test Booking creation with missing mandatory fields while keeping all optional fields.
     */
    @Test(dataProvider = "missingMandatoryWithAllOptional", groups = {"negative", "regression", "mandatoryFields"})
    public void shouldReturn400EWhenCreatingBookingWithMissingMandatoryFields(String startDate, String endDate,
                                                                              String propertyId, String id,
                                                                              String status, Guest guest) {
        SoftAssert softAssert = new SoftAssert();

        // Create a booking with missing mandatory fields, but all optional fields included
        Booking book = Booking.builder()
                .id(id)
                .startDate(startDate)
                .endDate(endDate)
                .propertyId(propertyId)
                .status(status)
                .guest(guest)
                .build();

        // Send request
        Response response = bookingService.bookingCreation(book);
        ValidationErrorResponse bookingResponse = response.as(ValidationErrorResponse.class);

        // Assert HTTP status code
        softAssert.assertEquals(response.getStatusCode(), 400, "Expected 400 Bad Request");

        // Assert response structure
        softAssert.assertEquals(bookingResponse.getType(), "https://www.hostfully.com/problems/validation-error", "Incorrect error type");
        softAssert.assertEquals(bookingResponse.getTitle(), "Validation Error", "Incorrect title");
        softAssert.assertEquals(bookingResponse.getStatus(), 400, "Incorrect status");
        softAssert.assertEquals(bookingResponse.getDetail(), "Validation failed", "Incorrect detail");
        softAssert.assertEquals(bookingResponse.getInstance(), "/bookings", "Incorrect instance");

        softAssert.assertAll();
    }


    /**
     * DataProvider for all positive test cases with different field combinations.
     */
    @DataProvider(name = "bookingCombinations")
    public Object[][] provideBookingCombinations() {
        String targetPropertyId = "e38698fc-2944-4c1a-9cc2-afbd5993d2ed";
        String startDate = getLastBookedDate(targetPropertyId);
        String endDate = DateTimeUtils.addOneDay(startDate);

        return new Object[][]{
                // Mandatory fields only
                {null, startDate, endDate, targetPropertyId, null, null},
                {null, startDate, endDate, targetPropertyId, "SCHEDULED", null},
                {null, startDate, endDate, targetPropertyId, null, new Guest("Diogo", "Pereira", "1984-05-18")},
                {UUID.randomUUID().toString(), startDate, endDate, targetPropertyId, null, null},
                {UUID.randomUUID().toString(), startDate, endDate, targetPropertyId, "SCHEDULED", null},
                {UUID.randomUUID().toString(), startDate, endDate, targetPropertyId, null, new Guest("Diogo", "Pereira", "1984-05-18")},
                {UUID.randomUUID().toString(), startDate, endDate, targetPropertyId, "SCHEDULED", new Guest("Diogo", "Pereira", "1984-05-18")}
        };
    }

    /**
     * Test Booking creation with different combinations of mandatory and optional fields.
     */
    @Test(dataProvider = "bookingCombinations", groups = {"positive", "regression"})
    public void shouldReturn201WhenCreatingBookingUsingMandatoryFieldsCombinationWithOptionalFieldsTest(String id, String startDate, String endDate, String propertyId,
                                                   String status, Guest guest) {
        SoftAssert softAssert = new SoftAssert();

        // Create a booking with varying fields
        Booking book = Booking.builder()
                .id(id)
                .startDate(startDate)
                .endDate(endDate)
                .propertyId(propertyId)
                .status(status)
                .guest(guest)
                .build();

        // Send request
        Response response = bookingService.bookingCreation(book);
        BookingResponse bookingResponse = response.as(BookingResponse.class);

        String expectedStartDate = DateTimeUtils.formatDate(bookingResponse.getStartDate());
        String expectedEndDate = DateTimeUtils.formatDate(bookingResponse.getEndDate());
        String expectedDateOfBirth = DateTimeUtils.formatDate(bookingResponse.getGuest().getDateOfBirth());

        // Assert HTTP status code
        softAssert.assertEquals(response.getStatusCode(), 201, "Expected 201 Created");

        // Validate JSON Response Fields
        softAssert.assertNotNull(bookingResponse.getId(), "Booking ID should not be null");
        softAssert.assertEquals(expectedStartDate, startDate, "Start date should match");
        softAssert.assertEquals(expectedEndDate, endDate, "End date should match");
        softAssert.assertEquals(bookingResponse.getStatus(), "SCHEDULED", "Status should be SCHEDULED");

        // Validate Guest Details
        softAssert.assertNotNull(bookingResponse.getGuest(), "Guest object should not be null");
        softAssert.assertEquals(bookingResponse.getGuest().getFirstName(), "Diogo", "First name should match");
        softAssert.assertEquals(bookingResponse.getGuest().getLastName(), "Pereira", "Last name should match");
        softAssert.assertEquals(expectedDateOfBirth, "1984-05-18", "Date of birth should match");

        // Validate Property ID
        softAssert.assertEquals(bookingResponse.getPropertyId(), propertyId, "Property ID should match");

        // Assert all to ensure all validations are executed
        softAssert.assertAll();
    }

    @DataProvider(name = "unavailableDates")
    public static Object[][] unavailableDates() {
        return new Object[][]{
                {"2025-06-01", "2025-06-01"}
        };
    }

    @Test(dataProvider = "unavailableDates", groups = {"negative", "regression"})
    public void shouldReturn422ForBookingDateUnavailableWhenGettingBookingCreationTest(String startDate, String endDate) {
        initSoftAssert();
        String newUUID = UUID.randomUUID().toString();
        String targetPropertyId = "201c3466-153a-403b-9434-e3ff413a84cc";

        // Validate booking details
        Booking book = Booking.builder()
                .id(newUUID)
                .startDate(startDate)
                .endDate(endDate)
                .status("SCHEDULED")
                .guest(Guest.builder()
                        .firstName("Diogo")
                        .lastName("Pereira")
                        .dateOfBirth("1984-05-18")
                        .build())
                .propertyId(targetPropertyId)
                .build();

        Response response = bookingService.bookingCreation(book);
        // Deserialize JSON response into BookingErrorResponse model
        BookingErrorResponse errorResponse = response.as(BookingErrorResponse.class);

        // Assertions on response status
        softAssert.assertEquals(response.getStatusCode(), 422, "Expected 422 Unprocessable Entity");

        // Assertions on error response fields
        softAssert.assertEquals(errorResponse.getType(), "https://www.hostfully.com/problems/invalid-booking", "Error type mismatch");
        softAssert.assertEquals(errorResponse.getTitle(), "Invalid Booking", "Error title mismatch");
        softAssert.assertEquals(errorResponse.getStatus(), 422, "Status code should match 422");
        softAssert.assertEquals(errorResponse.getDetail(), "Supplied booking is not valid", "Error detail mismatch");
        softAssert.assertEquals(errorResponse.getInstance(), "/bookings", "Instance path mismatch");
        softAssert.assertEquals(errorResponse.getBookingDatesUnavailable(), "BOOKING_DATES_UNAVAILABLE", "Booking dates unavailable error mismatch");

        // Assert all to ensure all validations are executed
        softAssert.assertAll();
    }

    /**
     * Retrieves the formatted endDate (YYYY-MM-DD) for the last occurrence of a given propertyId.
     *
     * @param targetPropertyId The propertyId to search for.
     * @return Formatted endDate (YYYY-MM-DD) or an error message.
     */
    public String getLastBookedDate(String targetPropertyId) {
        // Fetch API response
        Response response = bookingService.bookingRetrievalAll();

        // Deserialize JSON response into a list of BookingResponse objects
        List<BookingResponse> bookingResponses = response.as(new TypeRef<>() {});

        // Find the last occurrence of the given propertyId
        Optional<BookingResponse> lastBooking = bookingResponses.stream()
                .filter(booking -> booking.getPropertyId().equals(targetPropertyId))
                .reduce((first, second) -> second); // Get the last occurrence

        // Extract and format the endDate
        return lastBooking.map(booking -> DateTimeUtils.formatDate(booking.getEndDate()))
                .orElse("PropertyId not found in the API response.");
    }
}
