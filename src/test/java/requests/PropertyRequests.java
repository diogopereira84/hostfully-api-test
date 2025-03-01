package requests;

import static io.restassured.http.ContentType.JSON;
import io.restassured.response.Response;
import models.Property;

import static io.restassured.RestAssured.given;

public class PropertyRequests {

    public static Response createProperty(Property property) {
        return given()
                .contentType(JSON)
                .body(property)
                .post("/properties");
    }

    public static Response getProperty(String propertyId) {
        return given()
                .get("/properties/" + propertyId);
    }
}