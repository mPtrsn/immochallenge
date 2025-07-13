package com.malte.immochallenge.spotify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malte.immochallenge.spotify.exception.SpotifyApiException;
import com.malte.immochallenge.spotify.exception.SpotifyAuthException;
import com.malte.immochallenge.spotify.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyService {

    private final SpotifyConfiguration configuration;
    private final RestClient restClient;

    SpotifyAccessToken accessToken;

    private static final List<String> artistIds = List.of(
            "4oDjh8wNW5vDHyFRrDYC4k",
            "2FXC3k01G6Gw61bmprjgqS",
            "1t20wYnTiAT0Bs7H1hv9Wt",
            "6XyY86QOPPrYVGvF9ch6wz",
            "6eXZu6O7nAUA5z6vLV8NKI",
            "5SHgclK1ZpTdfdAmXW7J6s",
            "5wD0owYApRtYmjPWavWKvb",
            "6I8TDGeUmmLom8auKPzMdX",
            "4LLpKhyESsyAXpc4laK94U",
            "7Ln80lUS6He07XvHI8qqHH",
            "21mKp7DqtSNHhCAU2ugvUw"
    );

    public SpotifyArtistsResponse getArtistFromApi() {
        if (accessToken == null || accessTokenIsNotValid()) {
            getAccessToken();
        }
        return getArtists();
    }

    private SpotifyArtistsResponse getArtists() {
        var response = restClient.get()
                .uri("https://api.spotify.com/v1/artists?ids={ids}", String.join(",", artistIds))
                .header("Authorization", "Bearer " + accessToken.getToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, SpotifyService::handleApiError)
                .toEntity(SpotifyArtistsResponse.class);
        return response.getBody();
    }

    private void getAccessToken() {
        log.info("Refreshing access token");
        ResponseEntity<AccessTokenResponse> response = restClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=client_credentials&client_id=" + configuration.getClientId() + "&client_secret=" + configuration.getClientSecret())
                .retrieve()
                .onStatus(HttpStatusCode::isError, SpotifyService::handleAuthError)
                .toEntity(AccessTokenResponse.class);
        if (response.getBody() != null) {
            var accessTokenResponse = response.getBody();
            accessToken = SpotifyAccessToken.builder()
                    .token(accessTokenResponse.getAccess_token())
                    .tokenType(accessTokenResponse.getToken_type())
                    .expiresIn(accessTokenResponse.getExpires_in())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    private boolean accessTokenIsNotValid() {
        return accessToken.getTimestamp().plusSeconds(accessToken.getExpiresIn()).isBefore(LocalDateTime.now());
    }

    private static void handleApiError(HttpRequest request, ClientHttpResponse errorResponse) throws IOException {
        SpotifyApiErrorResponse apiError = new ObjectMapper().readValue(errorResponse.getBody(), SpotifyApiErrorResponse.class);
        throw new SpotifyApiException(HttpStatus.resolve(apiError.getError().getStatus()), apiError.getError().getMessage());
    }

    private static void handleAuthError(HttpRequest request, ClientHttpResponse errorResponse) throws IOException {
        SpotifyAuthenticationErrorResponse authError = new ObjectMapper().readValue(errorResponse.getBody(), SpotifyAuthenticationErrorResponse.class);
        throw new SpotifyAuthException(authError.getError(), authError.getError_description());
    }
}
