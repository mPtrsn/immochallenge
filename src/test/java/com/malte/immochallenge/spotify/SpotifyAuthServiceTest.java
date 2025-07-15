package com.malte.immochallenge.spotify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malte.immochallenge.spotify.exception.SpotifyAuthException;
import com.malte.immochallenge.spotify.response.SpotifyAccessTokenResponse;
import com.malte.immochallenge.spotify.response.SpotifyAuthenticationErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RestClientTest(SpotifyAuthService.class)
class SpotifyAuthServiceTest {

    @Autowired
    MockRestServiceServer server;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    SpotifyConfiguration configuration;

    @Autowired
    SpotifyAuthService spotifyAuthService;

    @BeforeEach
    void resetMockServer() {
        server.reset();
    }

    @Test
    @DisplayName("should make an access token request to the api")
    void getAccessToken1() throws JsonProcessingException {
        SpotifyAccessTokenResponse response = SpotifyAccessTokenResponse.builder()
                .access_token("token")
                .token_type("type")
                .expires_in(1L)
                .build();

        server.expect(requestTo("https://accounts.spotify.com/api/token"))
                .andRespond(withSuccess(mapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        var spotifyToken = spotifyAuthService.getAccessToken();

        assertThat(spotifyToken).isNotNull();
        assertThat(spotifyToken.getToken()).isEqualTo("token");
        assertThat(spotifyToken.getTokenType()).isEqualTo("type");
        assertThat(spotifyToken.getExpiresIn()).isEqualTo(1L);
        assertThat(spotifyToken.getTimestamp()).isNotNull();

        server.verify();
    }

    @Test
    @DisplayName("should make an access token request to the api only once if token is still valid")
    void getAccessToken2() throws JsonProcessingException {
        SpotifyAccessTokenResponse response = SpotifyAccessTokenResponse.builder()
                .access_token("token")
                .token_type("type")
                .expires_in(1L)
                .build();

        server.expect(ExpectedCount.max(1), requestTo("https://accounts.spotify.com/api/token"))
                .andRespond(withSuccess(mapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        var spotifyToken = spotifyAuthService.getAccessToken();

        assertThat(spotifyToken).isNotNull();
        var secondSpotifyToken = spotifyAuthService.getAccessToken();
        assertThat(spotifyToken).isEqualTo(secondSpotifyToken);

        server.verify();
    }

    @Test
    @DisplayName("should handle errors correctly")
    void getAccessToken3() throws JsonProcessingException {
        SpotifyAuthenticationErrorResponse error = SpotifyAuthenticationErrorResponse.builder().error("Error").error_description("description").build();

        server.expect(requestTo("https://accounts.spotify.com/api/token"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR).body(mapper.writeValueAsString(error)));

        assertThatThrownBy(() -> spotifyAuthService.getAccessToken()).isInstanceOf(SpotifyAuthException.class)
                .hasMessageContaining("Error: description");

        server.verify();
    }

    @Test
    @DisplayName("should handle errors correctly")
    void getAccessToken4() {
        server.expect(requestTo("https://accounts.spotify.com/api/token"))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> spotifyAuthService.getAccessToken()).isInstanceOf(SpotifyAuthException.class)
                .hasMessageContaining("unexpected error: body was empty on success. this should not happen");

        server.verify();
    }
}