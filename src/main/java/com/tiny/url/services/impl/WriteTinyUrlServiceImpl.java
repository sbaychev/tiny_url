package com.tiny.url.services.impl;

import static java.time.temporal.ChronoUnit.DAYS;

import com.tiny.url.data.TinyUrl;
import com.tiny.url.repository.ITinyUrlRepository;
import com.tiny.url.services.IWriteTinyUrlService;
import java.time.Instant;
import java.util.Base64;
import org.h2.security.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
//@Scope(SCOPE_PROTOTYPE)
public class WriteTinyUrlServiceImpl implements IWriteTinyUrlService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WriteTinyUrlServiceImpl.class);

    public static final String HTTP_LOCALHOST_8081 = "http://localhost:8081/";

    private final ITinyUrlRepository iTinyUrlRepository;

    @Autowired
    public WriteTinyUrlServiceImpl(ITinyUrlRepository iTinyUrlRepository) {
        this.iTinyUrlRepository = iTinyUrlRepository;
    }

    @Override
    public String createTinyUrl(String originalUrl) {

        String theTinyUrl = generateTinyUrl(originalUrl);

        LOGGER.info("Original Url {}", originalUrl);

        TinyUrl tinyUrl = TinyUrl.builder()
            .theTinyUrl(theTinyUrl)
            .timesAccessed(0)
            .insertionTime(Instant.now())
            .expirationTime(Instant.now().plus(30, DAYS))
            .originalUrl(originalUrl).build();

        iTinyUrlRepository.saveAndFlush(tinyUrl);
        iTinyUrlRepository.updateTinySetTimesAccessedForTiny(theTinyUrl);

        LOGGER.info("Shortened Url {}{}", HTTP_LOCALHOST_8081, theTinyUrl);

        return HTTP_LOCALHOST_8081 + theTinyUrl;
    }

    @Override
    public void deleteTinyUrl(TinyUrl aTinyUrl) {
        iTinyUrlRepository.delete(aTinyUrl);
    }

    @Override
    public void updateTimesAccessed(String tinyUrl, String originalUrl) {
        iTinyUrlRepository.updateTinySetTimesAccessedForTiny(tinyUrl);
    }

    public static String generateTinyUrl(final String originalUrl) {

        // we first do one way hashing 32 bytes almost unique hash
        final byte[] hash = SHA256.getHash(originalUrl.getBytes(), false);

        // for simplicity and readability we do base64 mapping
        final String hashed = Base64.getEncoder().encodeToString(hash);

        // result of hashed substring + "random num"
        // -> 64^9+ = eighteen quadrillion fourteen trillion possible combinations
        return hashed.substring(0, 8) + System.currentTimeMillis();
    }
}
