package com.tiny.url.services.impl;

import com.tiny.url.data.TinyUrl;
import com.tiny.url.exceptions.NoSuchElementFoundException;
import com.tiny.url.repository.ITinyUrlRepository;
import com.tiny.url.services.IReadTinyUrlService;
import com.tiny.url.services.IWriteTinyUrlService;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service
public class ReadTinyUrlServiceImpl implements IReadTinyUrlService {

    private static final Logger LOG = LoggerFactory.getLogger(ReadTinyUrlServiceImpl.class);

    private static final String THERE_IS_NO_SUCH_REQUESTED_TINY_URL = "There is no such Requested Tiny Url: ";

    private final ITinyUrlRepository iTinyUrlRepository;

    private final IWriteTinyUrlService iWriteTinyUrlService;

    @Autowired
    public ReadTinyUrlServiceImpl(ITinyUrlRepository iTinyUrlRepository, IWriteTinyUrlService iWriteTinyUrlService) {
        this.iTinyUrlRepository = iTinyUrlRepository;
        this.iWriteTinyUrlService = iWriteTinyUrlService;
    }

    @Override
    public String getOriginalUrl(String tinyUrl) {

        final TinyUrl aTinyUrl = iTinyUrlRepository.findByTheTinyUrl(tinyUrl);

        String original = aTinyUrl != null ? aTinyUrl.getOriginalUrl() : "";

        if (aTinyUrl == null || original.isEmpty()) {
            throw new NoSuchElementFoundException(THERE_IS_NO_SUCH_REQUESTED_TINY_URL + tinyUrl);
        }

        if (aTinyUrl.getExpirationTime().isBefore(getNow())) {
            // consider using the spring service message bus or proper jms
            new Thread(() -> iWriteTinyUrlService.deleteTinyUrl(aTinyUrl)).start();
            throw new NoSuchElementFoundException(THERE_IS_NO_SUCH_REQUESTED_TINY_URL + tinyUrl);
        } else {
            // consider using the spring service message bus or proper jms
            new Thread(() -> iWriteTinyUrlService.updateTimesAccessed(tinyUrl, aTinyUrl.getOriginalUrl())).start();
        }

        return original;
    }

    /**
     * @return simple Instant now
     */
    public Instant getNow() {
        return Instant.now();
    }

    @Override
    @Async
    public Future<List<TinyUrl>> getAllTinyUrls() {
        LOG.info("Request to get a list of tiny urls");
        return new AsyncResult(iTinyUrlRepository.findAll());
    }

    @Override
    public int getTimesAccessedTinyUrl(String tinyUrl) {

        int count;

        try {
            count = iTinyUrlRepository.findByTheTinyUrl(tinyUrl).getTimesAccessed();
        } catch (Exception exception) {
            throw new NoSuchElementFoundException(THERE_IS_NO_SUCH_REQUESTED_TINY_URL + tinyUrl);
        }

        return count;
    }
}
