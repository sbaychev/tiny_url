package com.tiny.url;

import static com.tiny.url.TestUtils.getTheTinyUrlEntity;
import static com.tiny.url.services.impl.WriteTinyUrlServiceImpl.HTTP_LOCALHOST_8081;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.tiny.url.data.TinyUrl;
import com.tiny.url.exceptions.NoSuchElementFoundException;
import com.tiny.url.repository.ITinyUrlRepository;
import com.tiny.url.services.impl.ReadTinyUrlServiceImpl;
import com.tiny.url.services.impl.WriteTinyUrlServiceImpl;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ReadWriteServiceTest {

    private ReadTinyUrlServiceImpl readTinyUrlService;

    private WriteTinyUrlServiceImpl writeTinyUrlService;

    @Mock
    private WriteTinyUrlServiceImpl writeTinyUrlServiceMock;

    @Mock
    private ITinyUrlRepository iTinyUrlRepository;

    @BeforeEach
    public void init() {

        MockitoAnnotations.openMocks(this);

        writeTinyUrlService = Mockito.spy(new WriteTinyUrlServiceImpl(iTinyUrlRepository));
        readTinyUrlService = Mockito.spy(new ReadTinyUrlServiceImpl(iTinyUrlRepository, writeTinyUrlServiceMock));
    }

    @Test
    public void whenGetAllTinyUrlsMethodIsCalled_thenResultIsCorrect() throws ExecutionException,
        InterruptedException {

        TinyUrl aTinyUrl = getTheTinyUrlEntity();

        doReturn((CompletableFuture.completedFuture(new ArrayList<>(Collections.singletonList(aTinyUrl)))))
            .when(readTinyUrlService).getAllTinyUrls();

        Future tinyUrls = readTinyUrlService.getAllTinyUrls();
        assertEquals(List.of(aTinyUrl), tinyUrls.get());
    }

    @Test
    public void whenGetATinyUrlsMethodIsCalled_thenResultIsCorrect() {

        TinyUrl aTinyUrl = getTheTinyUrlEntity();

        doReturn(aTinyUrl).when(iTinyUrlRepository).findByTheTinyUrl(aTinyUrl.getTheTinyUrl());

        String originalUrl = readTinyUrlService.getOriginalUrl(aTinyUrl.getTheTinyUrl());
        assertEquals(aTinyUrl.getOriginalUrl(), originalUrl);
    }

    @Test
    public void whenGetCountTinyUrlHasBeenAccessed_thenResultIsCorrect() {

        TinyUrl aTinyUrl = getTheTinyUrlEntity();
        aTinyUrl.setTimesAccessed(1);

        doReturn(aTinyUrl).when(iTinyUrlRepository).findByTheTinyUrl(aTinyUrl.getTheTinyUrl());

        int count = readTinyUrlService.getTimesAccessedTinyUrl(aTinyUrl.getTheTinyUrl());
        assertEquals(count, aTinyUrl.getTimesAccessed());
    }

    @Test
    public void whenGetOriginalUrlsMethodIsCalled31DaysAfterCreation_ThenException() {

        TinyUrl aTinyUrl = getTheTinyUrlEntity();

        doReturn(aTinyUrl).when(iTinyUrlRepository).findByTheTinyUrl(aTinyUrl.getTheTinyUrl());
        doReturn((Instant.now().plus(31, DAYS))).when(readTinyUrlService).getNow();

        Exception exception = Assertions.assertThrows(NoSuchElementFoundException.class, () -> {
            readTinyUrlService.getOriginalUrl(aTinyUrl.getTheTinyUrl());
        });

        String expectedMessage = "There is no such Requested Tiny Url: theTinyUrl";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void whenCreateTinyUrlMethodIsCalled_ThenReturnProperResult() throws NoSuchAlgorithmException {

        TinyUrl aTinyUrl = getTheTinyUrlEntity();

        doReturn(aTinyUrl).when(iTinyUrlRepository).findByTheTinyUrl(aTinyUrl.getTheTinyUrl());

        String result = writeTinyUrlService.createTinyUrl(aTinyUrl.getOriginalUrl());
        assertTrue(result.contains(HTTP_LOCALHOST_8081));
        // magic 9 == 8 SHA256 hashed and Base64 encoded + random number 1 - 100
        assertTrue(result.length() >= (HTTP_LOCALHOST_8081.length() + 9));
    }

    @Test
    public void whenCountTimesAccessedOnNonExistingTinyUrlsIsCalled_ThenException() {

        doThrow(RuntimeException.class).when(iTinyUrlRepository).findByTheTinyUrl("unknownTinyUrl");

        Exception exception = Assertions.assertThrows(NoSuchElementFoundException.class, () -> {
            readTinyUrlService.getTimesAccessedTinyUrl("unknownTinyUrl");
        });

        String expectedMessage = "There is no such Requested Tiny Url: unknownTinyUrl";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
