package com.malte.immochallenge;

import com.malte.immochallenge.album.AlbumService;
import com.malte.immochallenge.album.model.Album;
import com.malte.immochallenge.album.model.SimplifiedArtist;
import com.malte.immochallenge.artist.ArtistService;
import com.malte.immochallenge.artist.model.Artist;
import com.malte.immochallenge.model.Image;
import com.malte.immochallenge.spotify.SpotifyService;
import com.malte.immochallenge.spotify.exception.SpotifyApiException;
import com.malte.immochallenge.spotify.model.*;
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
    @Captor
    ArgumentCaptor<List<Album>> albumCaptor;


    @Test
    public void getSpotifyDataPeriodically1() {
        Mockito.when(spotifyService.getDataFromSpotify()).thenReturn(SpotifyApiResponse.builder()
                .artists(List.of(getSpotifyArtist()))
                .albums(List.of(getSpotifyAlbum()))
                .build());

        dataFetchingService.getSpotifyDataPeriodically();

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

        verify(albumService).handleNewAlbums(albumCaptor.capture(), any());
        assertThat(albumCaptor.getValue()).hasSize(1);
        assertThat(albumCaptor.getValue()).containsExactly(Album.builder()
                .id(null)
                .lastSynchronized(null)
                .lastSynchronized(null)
                .spotifyId("spotifyId")
                .name("name")
                .albumType("type")
                .totalTracks(5)
                .releaseDate("releaseDate")
                .releaseDatePrecision("precision")
                .albumGroup("albumGroup")
                .artists(List.of(SimplifiedArtist.builder()
                        .externalUrl("externalUrl")
                        .href("href")
                        .spotifyId("artistId")
                        .name("name")
                        .type("type")
                        .spotifyUri("uri")
                        .build()))
                .restrictionReason(null)
                .images(List.of(Image.builder().url("image url").width(100).height(200).build()))
                .href("href")
                .spotifyUri("uri")
                .externalUrl("externalUrl")
                .build());


    }

    @Test
    public void getSpotifyDataPeriodically2() {
        Mockito.when(spotifyService.getDataFromSpotify()).thenThrow(new SpotifyApiException(HttpStatus.BAD_REQUEST, "Bad Request"));

        dataFetchingService.getSpotifyDataPeriodically();

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
                .external_urls(SpotifyExternalUrls.builder().spotify("externalUrl").build())
                .followers(SpotifyArtist.SpotifyFollowers.builder().href(null).total(100L).build())
                .genres(List.of("genre"))
                .images(List.of(SpotifyImage.builder().url("imageUrl").width(10).height(15).build()))
                .build();
    }

    private SpotifyAlbum getSpotifyAlbum() {
        return SpotifyAlbum.builder()
                .id("spotifyId")
                .name("name")
                .album_type("type")
                .total_tracks(5)
                .release_date("releaseDate")
                .release_date_precision("precision")
                .album_group("albumGroup")
                .artists(List.of(SpotifyAlbum.SimplifiedArtist.builder()
                        .external_urls(SpotifyExternalUrls.builder().spotify("externalUrl").build())
                        .href("href")
                        .id("artistId")
                        .name("name")
                        .type("type")
                        .uri("uri")
                        .build()))
                .restrictions(null)
                .images(List.of(SpotifyImage.builder().url("image url").width(100).height(200).build()))
                .href("href")
                .uri("uri")
                .external_urls(SpotifyExternalUrls.builder().spotify("externalUrl").build())
                .build();
    }
}