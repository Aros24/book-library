package com.bookrental.service;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ServiceUtil {

    public String generateRandomUUID() {
        return UUID.randomUUID().toString(); // length = 36
    }

}
