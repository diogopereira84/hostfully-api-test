package base;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import config.Authentication;

public class BaseTest {
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://qa-assessment.svc.hostfully.com";
        Authentication.setAuth();
    }
}