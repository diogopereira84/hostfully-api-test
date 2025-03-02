package interfaces;

import io.restassured.response.Response;
import models.request.Property;

//Applies Dependency Inversion Principle (DIP), Allows flexible implementations
public interface IPropertyService {
    Response propertyRetrieval(String propertyId);
    Response propertyRetrievalAll();
    Response propertyCreation(Property property);
}
