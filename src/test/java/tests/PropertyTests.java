package tests;

import base.BaseTest;
import com.google.gson.Gson;
import io.restassured.response.Response;
import models.Property;
import org.testng.Assert;
import org.testng.annotations.Test;
import requests.PropertyRequests;

public class PropertyTests extends BaseTest {

    @Test
    public void testGetProperty() {
        Response response = PropertyRequests.getProperty("4814adee-cd2e-4c70-921d-19b4f0cd527d");
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test
    public void testCreateValidProperty() {
        Property property = Property.builder()
                .name("Beach House")
                .address("123 Beach Ave")
                .build();

        Response response = PropertyRequests.createProperty(property);
        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    public void testCreateInvalidProperty() {
        Property property = Property.builder()
                .name("")
                .address("")
                .build();

        Response response = PropertyRequests.createProperty(property);
        Assert.assertEquals(response.getStatusCode(), 400);
    }
}