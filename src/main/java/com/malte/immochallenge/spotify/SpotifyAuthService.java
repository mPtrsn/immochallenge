package com.malte.immochallenge.spotify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malte.immochallenge.spotify.exception.SpotifyAuthException;
import com.malte.immochallenge.spotify.model.SpotifyAccessToken;
import com.malte.immochallenge.spotify.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyAuthService {

    private final SpotifyConfiguration configuration;
    private final RestClient restClient;

    private SpotifyAccessToken accessToken;

    public SpotifyAccessToken getAccessToken() {
        if (accessToken == null || accessTokenIsNotValid()) {
            accessToken = getAccessTokenFromApi();
        }
        return accessToken;
    }

    private SpotifyAccessToken getAccessTokenFromApi() {
        log.info("Refreshing spotify access token");
        ResponseEntity<SpotifyAccessTokenResponse> response = restClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=client_credentials&client_id=" + configuration.getClientId() + "&client_secret=" + configuration.getClientSecret())
                .retrieve()
                .onStatus(HttpStatusCode::isError, SpotifyAuthService::handleAuthError)
                .toEntity(SpotifyAccessTokenResponse.class);
        if (response.getBody() != null) {
            var accessTokenResponse = response.getBody();
            return SpotifyAccessToken.builder()
                    .token(accessTokenResponse.getAccess_token())
                    .tokenType(accessTokenResponse.getToken_type())
                    .expiresIn(accessTokenResponse.getExpires_in())
                    .timestamp(LocalDateTime.now())
                    .build();
        } else {
            throw new SpotifyAuthException("unexpected error", "body was empty on success. this should not happen");
        }
    }

    private boolean accessTokenIsNotValid() {
        return accessToken.getTimestamp().plusSeconds(accessToken.getExpiresIn()).isBefore(LocalDateTime.now());
    }


    private static void handleAuthError(HttpRequest request, ClientHttpResponse errorResponse) throws IOException {
        SpotifyAuthenticationErrorResponse authError = new ObjectMapper().readValue(errorResponse.getBody(), SpotifyAuthenticationErrorResponse.class);
        throw new SpotifyAuthException(authError.getError(), authError.getError_description());
    }
}
