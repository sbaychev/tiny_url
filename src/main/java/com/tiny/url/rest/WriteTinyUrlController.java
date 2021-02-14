package com.tiny.url.rest;

import com.tiny.url.rest.requests.UrlRequest;
import com.tiny.url.services.IWriteTinyUrlService;
import java.util.concurrent.Callable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WriteTinyUrlController {

    @Autowired
    private IWriteTinyUrlService iWriteTinyUrlService;

    @PostMapping("/tiny")
    public Callable<ResponseEntity> toTinyUrl(@RequestBody UrlRequest urlRequest) {

        return () -> new ResponseEntity(iWriteTinyUrlService.createTinyUrl(urlRequest.getOriginalUrl()), HttpStatus.CREATED);
    }
}
