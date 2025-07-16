package com.malte.immochallenge;

import com.malte.immochallenge.album.AlbumService;
import com.malte.immochallenge.artist.ArtistService;
import com.malte.immochallenge.artist.model.Artist;
import com.malte.immochallenge.model.Image;
import com.malte.immochallenge.spotify.SpotifyService;
import com.malte.immochallenge.spotify.exception.SpotifyApiException;
import com.malte.immochallenge.spotify.model.SpotifyApiResponse;
import com.malte.immochallenge.spotify.model.SpotifyArtist;
import com.malte.immochallenge.spotify.model.SpotifyExternalUrls;
import com.malte.immochallenge.spotify.model.SpotifyImage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DataFetchingServiceTest {

    @Mock
    SpotifyService spotifyService;
    @Mock
    ArtistService artistService;
    @Mock
    AlbumService albumService;
    @InjectMocks
    DataFetchingService dataFetchingService;

    @Captor
    ArgumentCaptor<List<Artist>> artistCaptor;


    @Test
    public void getArtistDataPeriodically1() {
        Mockito.when(spotifyService.getDataFromSpotify()).thenReturn(SpotifyApiResponse.builder()
                .artists(List.of(getSpotifyArtist()))
                .albums(List.of())
                .build());

        dataFetchingService.getArtistDataPeriodically();

        verify(artistService).handleNewArtists(artistCaptor.capture(), any());
        assertThat(artistCaptor.getValue()).hasSize(1);
        assertThat(artistCaptor.getValue()).containsExactly(Artist.builder()
                .id(null)
                .lastModified(null)
                .lastSynchronized(null)
                .spotifyId("id")
                .href("href")
                .spotifyUri("uri")
                .externalUrl("externalUrl")
                .name("name")
                .followers(100L)
                .genres(List.of("genre"))
                .popularity(1)
                .images(List.of(Image.builder().url("imageUrl").width(10).height(15).build()))
                .build());
    }

    @Test
    public void getArtistDataPeriodically2() {
        Mockito.when(spotifyService.getDataFromSpotify()).thenThrow(new SpotifyApiException(HttpStatus.BAD_REQUEST, "Bad Request"));

        dataFetchingService.getArtistDataPeriodically();

        verify(artistService, never()).handleNewArtists(artistCaptor.capture(), any());
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
}