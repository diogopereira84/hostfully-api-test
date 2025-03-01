package base;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import services.AuthenticationService;
import interfaces.IPropertyService;
import services.PropertyService;

//BaseTest for Dependency Injection
public class BaseTest {

    protected AuthenticationService authService;
    protected IPropertyService propertyService;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://qa-assessment.svc.hostfully.com";

        // Use environment variables for credentials
        String username = System.getenv("API_USERNAME");
        String password = System.getenv("API_PASSWORD");

        authService = new AuthenticationService(username, password);
        propertyService = new PropertyService();

        authService.setValidAuth();
    }
}
