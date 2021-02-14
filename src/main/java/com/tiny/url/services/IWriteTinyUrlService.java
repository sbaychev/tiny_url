package com.tiny.url.services;

import com.tiny.url.data.TinyUrl;
import java.security.NoSuchAlgorithmException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.SERIALIZABLE)
public interface IWriteTinyUrlService {

    /**
     *
     * @param originalUrl
     * @return
     */
    String createTinyUrl(String originalUrl) throws NoSuchAlgorithmException;

    /**
     *
     * @param aTinyUrl
     */
    void deleteTinyUrl(TinyUrl aTinyUrl);

    /**
     *
     * @param tinyUrl
     * @param originalUrl
     */
    void updateTimesAccessed(String tinyUrl, String originalUrl);
}
