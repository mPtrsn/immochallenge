package com.malte.immochallenge.spotify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malte.immochallenge.spotify.exception.SpotifyApiException;
import com.malte.immochallenge.spotify.response.SpotifyApiErrorResponse;
import com.malte.immochallenge.spotify.response.SpotifyArtistsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyService {

    private final RestClient restClient;
    private final SpotifyAuthService spotifyAuthService;

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
        var accessToken = spotifyAuthService.getAccessToken();
        var response = restClient.get()
                .uri("https://api.spotify.com/v1/artists?ids={ids}", String.join(",", artistIds))
                .header("Authorization", "Bearer " + accessToken.getToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, SpotifyService::handleApiError)
                .toEntity(SpotifyArtistsResponse.class);
        return response.getBody();
    }

    private static void handleApiError(HttpRequest request, ClientHttpResponse errorResponse) throws IOException {
        SpotifyApiErrorResponse apiError = new ObjectMapper().readValue(errorResponse.getBody(), SpotifyApiErrorResponse.class);
        throw new SpotifyApiException(HttpStatus.resolve(apiError.getError().getStatus()), apiError.getError().getMessage());
    }
}
