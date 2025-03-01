package services;

import interfaces.IPropertyService;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

//Encapsulates property API calls, Follows Interface Segregation Principle (ISP)
public class PropertyService implements IPropertyService {

    private static final String PROPERTY_ENDPOINT = "/properties/";

    @Override
    public Response getProperty(String propertyId) {
        return given()
                .get(PROPERTY_ENDPOINT + propertyId);
    }
}
