package base;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import services.AuthenticationService;
import interfaces.IPropertyService;
import services.PropertyService;
import utils.AppSettings;

// BaseTest for Dependency Injection with parameterized environment
public class BaseTest {

    protected AuthenticationService authService;
    protected IPropertyService propertyService;

    @BeforeClass
    public void setup() {
        // Load environment-specific settings
        String baseUri = AppSettings.get("API_BASE_URI", "https://qa-assessment.svc.hostfully.com");
        String username = AppSettings.get("API_USERNAME");
        String password = AppSettings.get("API_PASSWORD");

        RestAssured.baseURI = baseUri;
        authService = new AuthenticationService(username, password);
        propertyService = new PropertyService();

        authService.setValidAuth();
    }
}
