package services;

import utils.AppSettings;

public enum AuthRole {
    ADMIN_ROLE(AppSettings.get("ADMIN_USERNAME"), AppSettings.get("ADMIN_PASSWORD")),
    USER_ROLE(AppSettings.get("USER_USERNAME"), AppSettings.get("USER_PASSWORD"));

    private final String username;
    private final String password;

    AuthRole(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}