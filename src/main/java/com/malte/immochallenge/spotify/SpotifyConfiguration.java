package com.malte.immochallenge.spotify;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "spotify")
@Data
public class SpotifyConfiguration {

    String clientId;
    String clientSecret;

    List<String> artistIds;
}
