package tests;

import base.BaseTest;
import io.restassured.response.Response;
import models.response.PropertyResponse;
import models.response.ValidationErrorResponse;
import models.request.Property;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
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

    @Test(groups = {"positive", "regression"})
    public void shouldReturn201WhenCreatingValidPropertyTest() {
        String expectedUUID = UUID.randomUUID().toString();
        String expectedAlias = "Property for Hostfully: " + expectedUUID;
        String dateTime = DateTimeUtils.getCurrentUtcTimestamp();
        String expectedContryCode = "BR";

        Property property = Property.builder()
                .id(expectedUUID)
                .alias(expectedAlias)
                .countryCode(expectedContryCode)  // Country code is null in response
                .createdAt(dateTime)
                .build();

        Response response = propertyService.createProperty(property);

        // Deserialize response
        PropertyResponse propertyResponse = response.as(PropertyResponse.class);

        // Assert HTTP status
        Assert.assertEquals(response.getStatusCode(), 201, "Expected 201 Created");

        UUID ds = UUID.fromString(propertyResponse.getId());
        // Assert response fields
        Assert.assertTrue(UUIDUtils.isValidUUID(propertyResponse.getId()), "ID is not a valid UUID");
        Assert.assertEquals(propertyResponse.getAlias(), expectedAlias, "Alias mismatch");
        Assert.assertEquals(propertyResponse.getCountryCode(),expectedContryCode, "CountryCode mismatch");

        // Assert createdAt array values
        List<Integer> expectedCreatedAt = Arrays.asList(2025, 3, 1, 23, 37, 6, 536538509);
        Assert.assertEquals(propertyResponse.getCreatedAt(), expectedCreatedAt, "CreatedAt mismatch");
    }

    @Test(dataProvider = "invalidUUIDs", groups = {"negative", "regression"})
    public void shouldReturn400WhenGettingPropertyWithInvalidUUIDTest(String propertyId) {
        Response response = propertyService.getProperty(propertyId);
        Assert.assertEquals(response.getStatusCode(), 400, "Expected 400 Bad Request");

        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);
        Assert.assertEquals(validationErrorResponse.getType(), "about:blank");
        Assert.assertEquals(validationErrorResponse.getTitle(), "Bad Request");
        Assert.assertEquals(validationErrorResponse.getStatus(), 400);
        Assert.assertEquals(validationErrorResponse.getDetail(), "Failed to convert 'propertyId' with value: '" + propertyId + "'");
        Assert.assertEquals(validationErrorResponse.getInstance(), "/properties/" + propertyId);
    }

    @Test(groups = {"negative", "regression"})
    public void shouldReturn204WhenPropertyNotFoundTest() {
        String propertyId = UUID.randomUUID().toString();
        Response response = propertyService.getProperty(propertyId);
        Assert.assertEquals(response.getStatusCode(), 204, "Expected 204 No Content");
    }

    @Test(groups = {"positive", "regression"})
    public void shouldReturn200WhenGettingPropertyWithValidCredentialsTest() {
        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";
        Response response = propertyService.getProperty(propertyId);
        Assert.assertEquals(response.getStatusCode(), 200, "Expected 200 OK");
    }

    @Test(groups = {"negative", "security", "regression"})
    public void shouldReturn401WhenGettingPropertyWithNoCredentialsTest() {
        AuthenticationService.removeAuth();
        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";

        Response response = propertyService.getProperty(propertyId);
        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);

        Assert.assertEquals(response.getStatusCode(), 401, "Expected 401 Unauthorized");
        Assert.assertEquals(validationErrorResponse.getException(), "Full authentication is required to access this resource");
        Assert.assertEquals(validationErrorResponse.getPath(), "/properties/" + propertyId);
        Assert.assertEquals(validationErrorResponse.getError(), "Unauthorized");
        Assert.assertEquals(validationErrorResponse.getMessage(), "Error while authenticating your access");
        Assert.assertNotNull(validationErrorResponse.getTimestamp());
    }

    @Test(groups = {"negative", "security", "regression"})
    public void shouldReturn401WhenGettingPropertyWithInvalidCredentialsTest() {
        AuthenticationService.setInvalidAuth();
        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";

        Response response = propertyService.getProperty(propertyId);
        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);

        Assert.assertEquals(response.getStatusCode(), 401, "Expected 401 Unauthorized");
        Assert.assertEquals(validationErrorResponse.getException(), "Bad credentials");
        Assert.assertEquals(validationErrorResponse.getPath(), "/properties/" + propertyId);
        Assert.assertEquals(validationErrorResponse.getError(), "Unauthorized");
        Assert.assertEquals(validationErrorResponse.getMessage(), "Error while authenticating your access");
        Assert.assertNotNull(validationErrorResponse.getTimestamp());
    }
}