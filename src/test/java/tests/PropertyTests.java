package tests;

import base.BaseTest;
import io.restassured.response.Response;
import models.ErrorResponse;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import services.AuthenticationService;

import java.util.UUID;

public class PropertyTests extends BaseTest {

    @DataProvider(name = "invalidUUIDs")
    public static Object[][] invalidUUIDs() {
        return new Object[][]{
                {"34545"},
                {"4814adee-cd2e-4c70-921d-19b4f0cd527i"}
        };
    }

    @Test(dataProvider = "invalidUUIDs")
    public void shouldReturn400WhenGettingPropertyWithInvalidUUID(String propertyId) {
        Response response = propertyService.getProperty(propertyId);
        Assert.assertEquals(response.getStatusCode(), 400, "Expected 400 Bad Request");

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        Assert.assertEquals(errorResponse.getType(), "about:blank");
        Assert.assertEquals(errorResponse.getTitle(), "Bad Request");
        Assert.assertEquals(errorResponse.getStatus(), 400);
        Assert.assertEquals(errorResponse.getDetail(), "Failed to convert 'propertyId' with value: '" + propertyId + "'");
        Assert.assertEquals(errorResponse.getInstance(), "/properties/" + propertyId);
    }

    @Test
    public void shouldReturn204WhenPropertyNotFound() {
        String propertyId = UUID.randomUUID().toString();
        Response response = propertyService.getProperty(propertyId);
        Assert.assertEquals(response.getStatusCode(), 204, "Expected 204 No Content");
    }

    @Test
    public void shouldReturn200WhenGettingPropertyWithValidCredentials() {
        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";
        Response response = propertyService.getProperty(propertyId);
        Assert.assertEquals(response.getStatusCode(), 200, "Expected 200 OK");
    }

    @Test
    public void shouldReturn401WhenGettingPropertyWithNoCredentials() {
        AuthenticationService.removeAuth();
        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";

        Response response = propertyService.getProperty(propertyId);
        ErrorResponse errorResponse = response.as(ErrorResponse.class);

        Assert.assertEquals(response.getStatusCode(), 401, "Expected 401 Unauthorized");
        Assert.assertEquals(errorResponse.getException(), "Full authentication is required to access this resource");
        Assert.assertEquals(errorResponse.getPath(), "/properties/" + propertyId);
        Assert.assertEquals(errorResponse.getError(), "Unauthorized");
        Assert.assertEquals(errorResponse.getMessage(), "Error while authenticating your access");
        Assert.assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    public void shouldReturn401WhenGettingPropertyWithInvalidCredentials() {
        AuthenticationService.setInvalidAuth();
        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";

        Response response = propertyService.getProperty(propertyId);
        ErrorResponse errorResponse = response.as(ErrorResponse.class);

        Assert.assertEquals(response.getStatusCode(), 401, "Expected 401 Unauthorized");
        Assert.assertEquals(errorResponse.getException(), "Bad credentials");
        Assert.assertEquals(errorResponse.getPath(), "/properties/" + propertyId);
        Assert.assertEquals(errorResponse.getError(), "Unauthorized");
        Assert.assertEquals(errorResponse.getMessage(), "Error while authenticating your access");
        Assert.assertNotNull(errorResponse.getTimestamp());
    }
}
