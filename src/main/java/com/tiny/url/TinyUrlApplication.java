package com.tiny.url;

import static java.time.temporal.ChronoUnit.DAYS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tiny.url.data.TinyUrl;
import com.tiny.url.repository.ITinyUrlRepository;
import com.tiny.url.services.impl.WriteTinyUrlServiceImpl;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.Transactional;

@EnableAsync
@EnableJpaRepositories
@EntityScan
@ComponentScan
@SpringBootApplication
public class TinyUrlApplication {

    private static final Logger LOG = LoggerFactory.getLogger(TinyUrlApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TinyUrlApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        return objectMapper;
    }

    @Bean
    @Transactional
    CommandLineRunner init(ITinyUrlRepository iTinyUrlRepo) {

        return (args) -> {

            String theTinyUrl = WriteTinyUrlServiceImpl.generateTinyUrl("https://start.spring.io/");

            LOG.info("The Tiny Url: {}", theTinyUrl);

            TinyUrl aTinyUrl = TinyUrl.builder()
                .theTinyUrl(theTinyUrl)
                .timesAccessed(0)
                .originalUrl("https://start.spring.io/")
                .insertionTime(Instant.now())
                .expirationTime(Instant.now().plus(30, DAYS))
                .build();

            iTinyUrlRepo.saveAndFlush(aTinyUrl);

            LOG.info("Created some Default Data Templates to be used");
        };
    }
}
