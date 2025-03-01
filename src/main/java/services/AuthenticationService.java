package services;

import io.restassured.RestAssured;

//Improve AuthenticationService, Move credentials to environment variables for security, Make it an instance-based service for testability.
public class AuthenticationService {

    private final String username;
    private final String password;

    public AuthenticationService(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setValidAuth() {
        RestAssured.authentication = RestAssured.preemptive().basic(username, password);
    }

    public static void setInvalidAuth() {
        RestAssured.authentication = RestAssured.preemptive().basic("invalidUser", "invalidPass");
    }

    public static void removeAuth() {
        RestAssured.authentication = RestAssured.DEFAULT_AUTH;
    }
}