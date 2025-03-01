package config;

import io.restassured.RestAssured;

public class Authentication {
    public static void setAuth() {
        RestAssured.authentication = RestAssured.preemptive()
                .basic("candidate@hostfully.com", "NaX5k1wFadtkFf");
    }
}