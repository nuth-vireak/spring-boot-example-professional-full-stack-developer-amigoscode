package com.kakreak.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {

}
