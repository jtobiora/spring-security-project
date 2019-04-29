package com.security.demo.constants;

public enum SecurityConstants {
    SECRET("SecretKeyToGenJWTs"),
    EXPIRATION_TIME("172800000"),
    TOKEN_PREFIX("Bearer"),
    HEADER_STRING("Authorization"),
    SIGN_UP_URL("/users/sign-up");

    private String value;

    private SecurityConstants(String message) {
        this.value = message;
    }

    public String getValue() {
        return this.value;
    }
}
