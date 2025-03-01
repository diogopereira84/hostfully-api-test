package interfaces;

import io.restassured.response.Response;
import models.Property;

//Applies Dependency Inversion Principle (DIP), Allows flexible implementations
public interface IPropertyService {
    Response getProperty(String propertyId);
    Response createProperty(Property property);
}
