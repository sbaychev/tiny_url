package com.tiny.url;

import static java.time.temporal.ChronoUnit.DAYS;

import com.tiny.url.data.TinyUrl;
import java.time.Instant;

public class TestUtils {

    public static TinyUrl getTheTinyUrlEntity() {
        return TinyUrl.builder()
            .theTinyUrl("theTinyUrl") // we assume it is well generated
            .timesAccessed(0)
            .originalUrl("https://start.spring.io/")
            .insertionTime(Instant.now())
            .expirationTime(Instant.now().plus(30, DAYS))
            .build();
    }
}
