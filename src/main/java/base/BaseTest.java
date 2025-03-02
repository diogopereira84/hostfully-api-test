package base;

import interfaces.IBookingService;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import services.AuthenticationService;
import interfaces.IPropertyService;
import services.BookingService;
import services.PropertyService;
import utils.AppSettings;

// BaseTest for Dependency Injection with parameterized environment
public class BaseTest {

    protected AuthenticationService authService;
    protected IPropertyService propertyService;
    protected IBookingService bookingService;

    @BeforeClass
    public void setup() {
        // Load environment-specific settings
        String baseUri = AppSettings.get("API_BASE_URI", "https://qa-assessment.svc.hostfully.com");
        String username = AppSettings.get("ADMIN_USERNAME");
        String password = AppSettings.get("ADMIN_PASSWORD");

        RestAssured.baseURI = baseUri;
        authService = new AuthenticationService(username, password);
        propertyService = new PropertyService();
        bookingService = new BookingService();

        authService.setValidAuth();
    }
}
