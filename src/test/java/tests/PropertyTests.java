package tests;

import base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.authentication.NoAuthScheme;
import io.restassured.response.Response;
import models.ErrorResponse;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class PropertyTests extends BaseTest {


    @DataProvider(name = "invalidUUIDs")
    public static Object[][] errorResponseData() {
        return new Object[][]{
                {"34545"},
                {"4814adee-cd2e-4c70-921d-19b4f0cd527i"}
        };
    }

    @Test(dataProvider = "invalidUUIDs")
    public void shouldReturn400WhenGettingPropertyWithInvalidUUIDTest(String propertyId) {
        Response response = given()
                .when()
                .get("/properties/" + propertyId);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        Assert.assertEquals(response.getStatusCode(), 400, "Expected HTTP status code 400 for valid authentication");

        Assert.assertEquals(errorResponse.getType(), "about:blank", "Type message mismatch");
        Assert.assertEquals(errorResponse.getTitle(), "Bad Request", "Title mismatch");
        Assert.assertEquals(errorResponse.getStatus(), 400, "Status type mismatch");
        Assert.assertEquals(errorResponse.getDetail(), "Failed to convert 'propertyId' with value: '" + propertyId + "'", "Details mismatch");
        Assert.assertEquals(errorResponse.getInstance(), "/properties/" + propertyId, "Instance mismatch");
    }

    @Test
    public void shouldReturn204WhenGettingPropertyWithValidUUIDAndNotFoundTest() {
        String propertyId = UUID.randomUUID().toString();

        Response response = given()
                .when()
                .get("/properties/" + propertyId);
        Assert.assertEquals(response.getStatusCode(), 204, "Expected HTTP status code 204 no content when Property does not exist in DTO");

    }

    @Test
    public void shouldReturn200WhenGettingPropertyWithValidCredentialsTest() {

        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";

        Response response = given()
                .when()
                .get("/properties/" + propertyId);

        Assert.assertEquals(response.getStatusCode(), 200, "Expected HTTP status code 200 for valid authentication");
    }

    @Test
    public void shouldReturn401WhenGettingPropertyWithNoCredentialsTest() {
        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";

        RestAssured.authentication = new NoAuthScheme();
        Response response = given()
                .when()
                .get("/properties/" + propertyId);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        Assert.assertEquals(response.getStatusCode(), 401, "Expected HTTP status code 401 for property creation with invalid authentication");
        Assert.assertEquals(errorResponse.getException(), "Full authentication is required to access this resource", "Exception message mismatch");
        Assert.assertEquals(errorResponse.getPath(), "/properties/" + propertyId, "Path mismatch");
        Assert.assertEquals(errorResponse.getError(), "Unauthorized", "Error type mismatch");
        Assert.assertEquals(errorResponse.getMessage(), "Error while authenticating your access", "Message mismatch");
        Assert.assertNotNull(errorResponse.getTimestamp(), "Timestamp should not be null");
    }

    @Test
    public void shouldReturn401WhenGettingPropertyWithInvalidCredentialsTest() {
        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";

        Response response = given()
                .auth()
                .preemptive()
                .basic("invalidUsername", "invalidPassword") // Replace with invalid credentials
                .when()
                .get("/properties/" + propertyId);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        Assert.assertEquals(response.getStatusCode(), 401, "Expected HTTP status code 401 for property creation with invalid authentication");
        Assert.assertEquals(errorResponse.getException(), "Bad credentials", "Exception message mismatch");
        Assert.assertEquals(errorResponse.getPath(), "/properties/" + propertyId, "Path mismatch");
        Assert.assertEquals(errorResponse.getError(), "Unauthorized", "Error type mismatch");
        Assert.assertEquals(errorResponse.getMessage(), "Error while authenticating your access", "Message mismatch");
        Assert.assertNotNull(errorResponse.getTimestamp(), "Timestamp should not be null");
    }
}