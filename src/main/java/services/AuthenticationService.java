package services;

import io.restassured.RestAssured;

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

    public static void setAuthRole(AuthRole role) {
        switch (role) {
            case ADMIN_ROLE:
            case USER_ROLE:
                RestAssured.authentication = RestAssured.preemptive().basic(role.getUsername(), role.getPassword());
                break;
            default:
                throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }

    public static void setInvalidAuth() {
        RestAssured.authentication = RestAssured.preemptive().basic("invalidUser", "invalidPass");
    }

    public static void removeAuth() {
        RestAssured.authentication = RestAssured.DEFAULT_AUTH;
    }
}