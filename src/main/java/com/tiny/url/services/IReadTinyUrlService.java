package com.tiny.url.services;

import java.util.concurrent.Future;

public interface IReadTinyUrlService {

    /**
     *
     */
    String getOriginalUrl(String tinyUrl);

    /**
     * @return a async collection of all tinyUrls
     */
    Future getAllTinyUrls();

    /**
     *
     * @param tinyUrl
     * @return
     */
    int getTimesAccessedTinyUrl(String tinyUrl);
}
