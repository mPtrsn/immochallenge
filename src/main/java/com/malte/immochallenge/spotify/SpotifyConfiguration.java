package com.malte.immochallenge.spotify;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spotify")
@Data
public class SpotifyConfiguration {

    String clientId;
    String clientSecret;
}
