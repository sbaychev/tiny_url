package com.tiny.url;

import static com.tiny.url.TestUtils.getTheTinyUrlEntity;
import static com.tiny.url.services.impl.WriteTinyUrlServiceImpl.HTTP_LOCALHOST_8081;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiny.url.data.TinyUrl;
import com.tiny.url.repository.ITinyUrlRepository;
import com.tiny.url.rest.requests.UrlRequest;
import com.tiny.url.services.IReadTinyUrlService;
import com.tiny.url.services.IWriteTinyUrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ReadWriteControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IWriteTinyUrlService iWriteTinyUrlService;

    @MockBean
    private IReadTinyUrlService iReadTinyUrlService;

    @MockBean
    private ITinyUrlRepository iTinyUrlRepository;

    @Test
    public void givenExistingTinyUrl_whenClientRequested_thenResourceRetrievedRedirected() throws Exception {

        TinyUrl aTinyUrl = getTheTinyUrlEntity();

        iTinyUrlRepository.save(aTinyUrl);

        given(this.iReadTinyUrlService.getOriginalUrl("theTinyUrl"))
            .willReturn("https://start.spring.io/");

        MvcResult mvcResult = this.mvc.perform(
            get("/{url}", "theTinyUrl"))
            .andExpect(status().is3xxRedirection()).andReturn();

        verify(iReadTinyUrlService, VerificationModeFactory.times(1)).getOriginalUrl(Mockito.any());
        reset(iReadTinyUrlService);
    }

    @Test
    public void givenNotExistingTinyUrl_whenClientRequestedAll_thenResourceRetrieved() throws Exception {

        ResultActions resultActions = this.mvc.perform(
            get("/tiny/all"))
            .andExpect(status().isOk());

        resultActions.andExpect(content().string(""));

        verify(iReadTinyUrlService, VerificationModeFactory.times(1)).getAllTinyUrls();
        reset(iReadTinyUrlService);
    }

    @Test
    public void givenExistingTinyUrl_whenClientRequestedCountCalls_thenResourceRetrieved() throws Exception {

        given(this.iReadTinyUrlService.getTimesAccessedTinyUrl("theTinyUrl"))
            .willReturn(1);

        MvcResult mvcResult = this.mvc.perform(
            get("/tiny/{url}", "theTinyUrl"))
            .andExpect(status().isFound()).andReturn();

        assertEquals(String.valueOf(1), mvcResult.getResponse().getContentAsString());

        verify(iReadTinyUrlService, VerificationModeFactory.times(1)).getTimesAccessedTinyUrl(Mockito.any());
        reset(iReadTinyUrlService);
    }

    @Test
    public void givenNotExistingTinyUrl_whenClientRequestedCreation_thenResourceCreatedAndRetrieved() throws Exception {

        given(this.iWriteTinyUrlService.createTinyUrl("https://start.spring.io/"))
            .willReturn(HTTP_LOCALHOST_8081 + "theTinyUrl");

        UrlRequest urlRequest = new UrlRequest("https://start.spring.io/");

        MvcResult mvcResult = this.mvc.perform(
            post("/tiny")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(urlRequest)))
            .andExpect(status().isOk()).andReturn();

        assertEquals("<201 CREATED Created,http://localhost:8081/theTinyUrl,[]>",
            mvcResult.getAsyncResult().toString());

        verify(iWriteTinyUrlService, VerificationModeFactory.times(1)).createTinyUrl(Mockito.any());
        reset(iWriteTinyUrlService);
    }
}
