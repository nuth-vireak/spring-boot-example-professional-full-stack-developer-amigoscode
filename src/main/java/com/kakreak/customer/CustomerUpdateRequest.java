package com.kakreak.customer;

public record CustomerUpdateRequest (
        String name,
        String email,
        Integer age
) {}
