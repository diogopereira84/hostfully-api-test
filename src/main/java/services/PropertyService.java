package services;

import interfaces.IPropertyService;
import io.restassured.response.Response;
import models.request.Property;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

//Encapsulates property API calls, Follows Interface Segregation Principle (ISP)
public class PropertyService implements IPropertyService {

    private static final String PROPERTY_ENDPOINT = "/properties";

    @Override
    public Response propertyRetrieval(String propertyId) {
        return given()
                .get(PROPERTY_ENDPOINT + "/" + propertyId);
    }

    @Override
    public Response propertyRetrievalAll() {
        return given()
                .get(PROPERTY_ENDPOINT);
    }

    @Override
    public Response propertyCreation(Property property) {
        return given()
                .contentType(JSON)
                .body(property)
                .post(PROPERTY_ENDPOINT);
    }
}
