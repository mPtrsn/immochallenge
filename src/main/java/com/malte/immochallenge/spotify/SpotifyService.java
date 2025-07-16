package com.malte.immochallenge.spotify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malte.immochallenge.spotify.exception.SpotifyApiException;
import com.malte.immochallenge.spotify.model.SpotifyAlbum;
import com.malte.immochallenge.spotify.model.SpotifyApiResponse;
import com.malte.immochallenge.spotify.model.SpotifyArtist;
import com.malte.immochallenge.spotify.response.SpotifyAlbumsResponse;
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
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyService {

    private final RestClient restClient;
    private final SpotifyAuthService spotifyAuthService;
    private final SpotifyConfiguration configuration;


    public SpotifyApiResponse getDataFromSpotify() {
        return SpotifyApiResponse.builder()
                .artists(getArtistFromApi())
                .albums(getAlbumsFromApi())
                .build();
    }

    public List<SpotifyArtist> getArtistFromApi() {
        var accessToken = spotifyAuthService.getAccessToken();
        var artistsResponse = restClient.get()
                .uri("https://api.spotify.com/v1/artists?ids={ids}", String.join(",", configuration.getArtistIds()))
                .header("Authorization", "Bearer " + accessToken.getToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, SpotifyService::handleApiError)
                .toEntity(SpotifyArtistsResponse.class);
        if (artistsResponse.getBody() == null || artistsResponse.getBody().getArtists() == null) {
            throw new SpotifyApiException((HttpStatus) artistsResponse.getStatusCode(), "body was empty on success. this should not happen");
        }
        return artistsResponse.getBody().getArtists();
    }

    public List<SpotifyAlbum> getAlbumsFromApi() {
        return configuration.getArtistIds().stream()
                .map(this::getAlbumsForArtistsFromApi)
                .map(SpotifyAlbumsResponse::getItems)
                .flatMap(Collection::stream)
                .toList();
    }

    private SpotifyAlbumsResponse getAlbumsForArtistsFromApi(String artistId) {
        var accessToken = spotifyAuthService.getAccessToken();

        var response = restClient.get()
                .uri("https://api.spotify.com/v1/artists/{id}/albums", artistId)
                .header("Authorization", "Bearer " + accessToken.getToken())
                .retrieve()
                .onStatus(HttpStatusCode::isError, SpotifyService::handleApiError)
                .toEntity(SpotifyAlbumsResponse.class);
        return response.getBody();
    }

    private static void handleApiError(HttpRequest request, ClientHttpResponse errorResponse) throws IOException {
        SpotifyApiErrorResponse apiError = new ObjectMapper().readValue(errorResponse.getBody(), SpotifyApiErrorResponse.class);
        throw new SpotifyApiException(HttpStatus.resolve(apiError.getError().getStatus()), apiError.getError().getMessage());
    }
}
