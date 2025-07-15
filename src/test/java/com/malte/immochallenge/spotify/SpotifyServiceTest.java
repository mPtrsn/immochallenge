package com.malte.immochallenge.spotify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malte.immochallenge.spotify.exception.SpotifyApiException;
import com.malte.immochallenge.spotify.model.*;
import com.malte.immochallenge.spotify.response.SpotifyApiErrorResponse;
import com.malte.immochallenge.spotify.response.SpotifyArtistsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(SpotifyService.class)
class SpotifyServiceTest {

    @Autowired
    MockRestServiceServer server;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    SpotifyAuthService spotifyAuthService;


    @Autowired
    SpotifyService spotifyService;

    String url = "https://api.spotify.com/v1/artists?ids=4oDjh8wNW5vDHyFRrDYC4k%2C2FXC3k01G6Gw61bmprjgqS%2C1t20wYnTiAT0Bs7H1hv9Wt%2C6XyY86QOPPrYVGvF9ch6wz%2C6eXZu6O7nAUA5z6vLV8NKI%2C5SHgclK1ZpTdfdAmXW7J6s%2C5wD0owYApRtYmjPWavWKvb%2C6I8TDGeUmmLom8auKPzMdX%2C4LLpKhyESsyAXpc4laK94U%2C7Ln80lUS6He07XvHI8qqHH%2C21mKp7DqtSNHhCAU2ugvUw";

    @BeforeEach
    void resetMockServer() {
        server.reset();
    }

    @Test
    @DisplayName("shout get all artists from the api")
    void getArtistFromApi1() throws JsonProcessingException {
        Mockito.when(spotifyAuthService.getAccessToken()).thenReturn(SpotifyAccessToken.builder().token("").build());
        server.expect(requestTo(url))
                .andRespond(withSuccess(mapper.writeValueAsString(SpotifyArtistsResponse.builder().artists(List.of(getSpotifyArtist())).build()), MediaType.APPLICATION_JSON));

        var artistResponse = spotifyService.getArtistFromApi();

        assertThat(artistResponse).isNotNull();
        assertThat(artistResponse.getArtists()).hasSize(1);
        verify(spotifyAuthService).getAccessToken();

        server.verify();
    }

    @Test
    @DisplayName("should handle errors correctly")
    void getArtistFromApi2() throws JsonProcessingException {
        Mockito.when(spotifyAuthService.getAccessToken()).thenReturn(SpotifyAccessToken.builder().token("").build());
        SpotifyApiErrorResponse error = SpotifyApiErrorResponse.builder().error(SpotifyApiErrorResponse.ErrorBody.builder().status(500).message("error message").build()).build();

        server.expect(requestTo(url))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR).body(mapper.writeValueAsString(error)));

        assertThatThrownBy(() -> spotifyService.getArtistFromApi()).isInstanceOf(SpotifyApiException.class)
                .hasMessageContaining("500 INTERNAL_SERVER_ERROR: error message");

        server.verify();
    }

    private SpotifyArtist getSpotifyArtist() {
        return SpotifyArtist.builder()
                .id("id")
                .href("href")
                .uri("uri")
                .name("name")
                .type("type")
                .popularity(1)
                .external_urls(new SpotifyExternalUrls("externalUrl"))
                .followers(new SpotifyFollowers(null, 100L))
                .genres(List.of("genre"))
                .images(List.of(SpotifyImage.builder().url("imageUrl").width(10).height(15).build()))
                .build();
    }
}