package com.malte.immochallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@EnableScheduling
public class ImmochallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImmochallengeApplication.class, args);
    }

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    // this is only here to make testing the ret client easier
    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return (restClientBuilder -> restClientBuilder.baseUrl(""));
    }

}
