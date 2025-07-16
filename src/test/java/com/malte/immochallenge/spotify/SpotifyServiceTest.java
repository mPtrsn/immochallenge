package com.malte.immochallenge.spotify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malte.immochallenge.spotify.exception.SpotifyApiException;
import com.malte.immochallenge.spotify.model.*;
import com.malte.immochallenge.spotify.response.SpotifyAlbumsResponse;
import com.malte.immochallenge.spotify.response.SpotifyApiErrorResponse;
import com.malte.immochallenge.spotify.response.SpotifyArtistsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
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

    @MockitoBean
    SpotifyConfiguration configuration;

    @Autowired
    SpotifyService spotifyService;

    String artistUrl = "https://api.spotify.com/v1/artists?ids=test";
    String albumUrl = "https://api.spotify.com/v1/artists/test/albums";

    @BeforeEach
    void resetMockServer() {
        server.reset();
        when(configuration.getArtistIds()).thenReturn(List.of("test"));
    }

    @Test
    @DisplayName("should make all data requests")
    void getDataFromSpotify() throws JsonProcessingException {
        when(spotifyAuthService.getAccessToken()).thenReturn(SpotifyAccessToken.builder().token("").build());
        server.expect(requestTo(artistUrl))
                .andRespond(withSuccess(mapper.writeValueAsString(SpotifyArtistsResponse.builder().artists(List.of(getSpotifyArtist())).build()), MediaType.APPLICATION_JSON));
        server.expect(requestTo(albumUrl))
                .andRespond(withSuccess(mapper.writeValueAsString(SpotifyAlbumsResponse.builder().items(List.of(getSpotifyAlbum(), getSpotifyAlbum())).build()), MediaType.APPLICATION_JSON));

        var response = spotifyService.getDataFromSpotify();

        assertThat(response).isNotNull();
        assertThat(response.getArtists()).hasSize(1);
        assertThat(response.getAlbums()).hasSize(2);
        verify(spotifyAuthService, times(2)).getAccessToken();

        server.verify();

    }

    @Test
    @DisplayName("should get all artists from the api")
    void getArtistFromApi1() throws JsonProcessingException {
        when(spotifyAuthService.getAccessToken()).thenReturn(SpotifyAccessToken.builder().token("").build());
        server.expect(requestTo(artistUrl))
                .andRespond(withSuccess(mapper.writeValueAsString(SpotifyArtistsResponse.builder().artists(List.of(getSpotifyArtist())).build()), MediaType.APPLICATION_JSON));

        var artistResponse = spotifyService.getArtistFromApi();

        assertThat(artistResponse).isNotNull();
        assertThat(artistResponse).hasSize(1);
        verify(spotifyAuthService).getAccessToken();

        server.verify();
    }

    @Test
    @DisplayName("should handle errors correctly")
    void getArtistFromApi2() throws JsonProcessingException {
        when(spotifyAuthService.getAccessToken()).thenReturn(SpotifyAccessToken.builder().token("").build());
        SpotifyApiErrorResponse error = SpotifyApiErrorResponse.builder().error(SpotifyApiErrorResponse.ErrorBody.builder().status(500).message("error message").build()).build();

        server.expect(requestTo(artistUrl))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR).body(mapper.writeValueAsString(error)));

        assertThatThrownBy(() -> spotifyService.getArtistFromApi()).isInstanceOf(SpotifyApiException.class)
                .hasMessageContaining("500 INTERNAL_SERVER_ERROR: error message");

        server.verify();
    }

    @Test
    @DisplayName("should get all albums for one artists from the api")
    void getAlbumsFromApi1() throws JsonProcessingException {
        when(spotifyAuthService.getAccessToken()).thenReturn(SpotifyAccessToken.builder().token("").build());
        server.expect(requestTo(albumUrl))
                .andRespond(withSuccess(mapper.writeValueAsString(SpotifyAlbumsResponse.builder().items(List.of(getSpotifyAlbum())).build()), MediaType.APPLICATION_JSON));

        var albumsResponse = spotifyService.getAlbumsFromApi();

        assertThat(albumsResponse).isNotNull();
        assertThat(albumsResponse).hasSize(1);
        verify(spotifyAuthService).getAccessToken();

        server.verify();
    }

    @Test
    @DisplayName("should handle errors correctly")
    void getAlbumsFromApi2() throws JsonProcessingException {
        when(spotifyAuthService.getAccessToken()).thenReturn(SpotifyAccessToken.builder().token("").build());
        SpotifyApiErrorResponse error = SpotifyApiErrorResponse.builder().error(SpotifyApiErrorResponse.ErrorBody.builder().status(500).message("error message").build()).build();

        server.expect(requestTo(albumUrl))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR).body(mapper.writeValueAsString(error)));

        assertThatThrownBy(() -> spotifyService.getAlbumsFromApi()).isInstanceOf(SpotifyApiException.class)
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
                .followers(SpotifyArtist.SpotifyFollowers.builder().href(null).total(100L).build())
                .genres(List.of("genre"))
                .images(List.of(SpotifyImage.builder().url("imageUrl").width(10).height(15).build()))
                .build();
    }

    private SpotifyAlbum getSpotifyAlbum() {
        return SpotifyAlbum.builder()
                .id("spotifyId")
                .album_type("single")
                .total_tracks(2)
                .available_markets(List.of("DE"))
                .external_urls(SpotifyExternalUrls.builder().spotify("externalUrl").build())
                .href("href")
                .images(List.of(SpotifyImage.builder().url("imageUrl").width(10).height(15).build()))
                .name("albumName")
                .release_date("releaseDate")
                .release_date_precision("releaseDatePrecision")
                .restrictions(null)
                .type("type")
                .uri("uri")
                .artists(List.of(
                        SpotifyAlbum.SimplifiedArtist.builder()
                                .external_urls(SpotifyExternalUrls.builder()
                                        .spotify("externalUrl")
                                        .build())
                                .href("href")
                                .id("artistId")
                                .name("artistName")
                                .type("artistType")
                                .uri("artistUri")
                                .build()))
                .album_group("albumGroup")
                .build();
    }
}