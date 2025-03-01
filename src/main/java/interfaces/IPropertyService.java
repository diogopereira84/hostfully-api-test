package interfaces;

import io.restassured.response.Response;

//Applies Dependency Inversion Principle (DIP), Allows flexible implementations
public interface IPropertyService {
    Response getProperty(String propertyId);
}
