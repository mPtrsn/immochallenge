package com.malte.immochallenge.spotify;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class SpotifyService {


    public String getAccessToken() {
        RestClient defaultClient = RestClient.create();

        ResponseEntity<String> response = defaultClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=client_credentials&client_id=8ff17daa702e48c28dfdeb982bb69502&client_secret=ee85a5d64c474b28b072e14a3f6176f1")
                .retrieve()
                .toEntity(String.class);
        log.info("access token: {}", response.getBody());
        return response.getBody();
    }

    @Scheduled(cron = "0/60 0 0 ? * * *")
    public void getArtists() {
        // TODO if access token
        RestClient defaultClient = RestClient.create();

        ResponseEntity<String> response = defaultClient.get()
                .uri("https://api.spotify.com/v1/artists?ids={ids}", "2CIMQHirSU0MQqyYHq0eOx,57dN52uHvrHOxijzpIgu3E,1vCWHaC5f2uS3yhpwWbIA6")

                .header("Authorization", "Bearer pipapo")
                .retrieve()
                .toEntity(String.class);
        log.info(response.getBody());
    }
}
