package com.tiny.url.rest;

import com.tiny.url.exceptions.NoSuchElementFoundException;
import com.tiny.url.services.IReadTinyUrlService;
import java.net.URI;
import java.util.concurrent.Callable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadTinyUrlController {

    @Autowired
    private IReadTinyUrlService iReadTinyUrlService;

    @GetMapping("/{url}")
    @ResponseBody
    public ResponseEntity<Void> redirectToOriginalUrl(@Validated @PathVariable(value = "url") String url) {

        String originalUrl = iReadTinyUrlService.getOriginalUrl(url);

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create(originalUrl))
            .build();
    }

    @GetMapping(value = "/tiny/all", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Callable<ResponseEntity<?>> getAllTinyUrls() {
        return () -> new ResponseEntity(iReadTinyUrlService.getAllTinyUrls().get(), HttpStatus.FOUND);
    }

    @GetMapping("/tiny/{url}")
    @ResponseBody
    public ResponseEntity<Void> getTimesAccessedTinyUrl(@Validated @PathVariable(value = "url") String url) {

        return new ResponseEntity(iReadTinyUrlService.getTimesAccessedTinyUrl(url), HttpStatus.FOUND);
    }

    @ExceptionHandler(NoSuchElementFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNoSuchElementFoundException(NoSuchElementFoundException exception) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(exception.getMessage());
    }
}
