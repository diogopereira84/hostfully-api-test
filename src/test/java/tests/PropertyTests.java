package tests;

import base.BaseTest;
import io.restassured.response.Response;
import models.response.PropertyResponse;
import models.response.ValidationErrorResponse;
import models.request.Property;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import services.AuthenticationService;
import utils.DateTimeUtils;
import utils.UUIDUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Listeners(utils.TestListener.class)
public class PropertyTests extends BaseTest {

    @DataProvider(name = "invalidUUIDs")
    public static Object[][] invalidUUIDs() {
        return new Object[][]{
                {"34545"},
                {"4814adee-cd2e-4c70-921d-19b4f0cd527i"}
        };
    }

    private SoftAssert softAssert; // Declare SoftAssert globally for each test

    /**
     * Initializes SoftAssert before each test.
     */
    private void initSoftAssert() {
        softAssert = new SoftAssert();
    }

    @Test(groups = {"positive", "regression"})
    public void shouldReturn201WhenCreatingValidPropertyTest() {
        initSoftAssert();

        String expectedUUID = UUID.randomUUID().toString();
        String expectedAlias = "Property for Hostfully: " + expectedUUID;
        String dateTime = DateTimeUtils.getCurrentUtcTimestamp();
        String expectedCountryCode = "BR";

        Property property = Property.builder()
                .id(expectedUUID)
                .alias(expectedAlias)
                .countryCode(expectedCountryCode)
                .createdAt(dateTime)
                .build();

        Response response = propertyService.createProperty(property);
        PropertyResponse propertyResponse = response.as(PropertyResponse.class);

        // Assert HTTP status
        softAssert.assertEquals(response.getStatusCode(), 201, "Expected 201 Created");

        // Validate UUID format
        softAssert.assertTrue(UUIDUtils.isValidUUID(propertyResponse.getId()), "ID is not a valid UUID");

        // Assert response fields
        softAssert.assertEquals(propertyResponse.getAlias(), expectedAlias, "Alias mismatch");
        softAssert.assertEquals(propertyResponse.getCountryCode(), expectedCountryCode, "CountryCode mismatch");

        // Assert createdAt array values
        List<Integer> expectedCreatedAt = Arrays.asList(2025, 3, 1, 23, 37, 6, 536538509);
        softAssert.assertEquals(propertyResponse.getCreatedAt(), expectedCreatedAt, "CreatedAt mismatch");

        // Collect all assertion failures at the end
        softAssert.assertAll();
    }

    @Test(dataProvider = "invalidUUIDs", groups = {"negative", "regression"})
    public void shouldReturn400WhenGettingPropertyWithInvalidUUIDTest(String propertyId) {
        initSoftAssert();

        Response response = propertyService.getProperty(propertyId);
        softAssert.assertEquals(response.getStatusCode(), 400, "Expected 400 Bad Request");

        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);
        softAssert.assertEquals(validationErrorResponse.getType(), "about:blank");
        softAssert.assertEquals(validationErrorResponse.getTitle(), "Bad Request");
        softAssert.assertEquals(validationErrorResponse.getStatus(), 400);
        softAssert.assertEquals(validationErrorResponse.getDetail(), "Failed to convert 'propertyId' with value: '" + propertyId + "'");
        softAssert.assertEquals(validationErrorResponse.getInstance(), "/properties/" + propertyId);

        softAssert.assertAll();
    }

    @Test(groups = {"negative", "regression"})
    public void shouldReturn204WhenPropertyNotFoundTest() {
        initSoftAssert();

        String propertyId = UUID.randomUUID().toString();
        Response response = propertyService.getProperty(propertyId);
        softAssert.assertEquals(response.getStatusCode(), 204, "Expected 204 No Content");

        softAssert.assertAll();
    }

    @Test(groups = {"positive", "regression"})
    public void shouldReturn200WhenGettingPropertyWithValidCredentialsTest() {
        initSoftAssert();

        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";
        Response response = propertyService.getProperty(propertyId);
        softAssert.assertEquals(response.getStatusCode(), 200, "Expected 200 OK");

        softAssert.assertAll();
    }

    @Test(groups = {"negative", "security", "regression"})
    public void shouldReturn401WhenGettingPropertyWithNoCredentialsTest() {
        initSoftAssert();

        AuthenticationService.removeAuth();
        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";

        Response response = propertyService.getProperty(propertyId);
        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);

        softAssert.assertEquals(response.getStatusCode(), 401, "Expected 401 Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getException(), "Full authentication is required to access this resource");
        softAssert.assertEquals(validationErrorResponse.getPath(), "/properties/" + propertyId);
        softAssert.assertEquals(validationErrorResponse.getError(), "Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getMessage(), "Error while authenticating your access");
        softAssert.assertNotNull(validationErrorResponse.getTimestamp());

        softAssert.assertAll();
    }

    @Test(groups = {"negative", "security", "regression"})
    public void shouldReturn401WhenGettingPropertyWithInvalidCredentialsTest() {
        initSoftAssert();

        AuthenticationService.setInvalidAuth();
        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";

        Response response = propertyService.getProperty(propertyId);
        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);

        softAssert.assertEquals(response.getStatusCode(), 401, "Expected 401 Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getException(), "Bad credentials");
        softAssert.assertEquals(validationErrorResponse.getPath(), "/properties/" + propertyId);
        softAssert.assertEquals(validationErrorResponse.getError(), "Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getMessage(), "Error while authenticating your access");
        softAssert.assertNotNull(validationErrorResponse.getTimestamp());

        softAssert.assertAll();
    }
}