package com.malte.immochallenge.artist;

import com.malte.immochallenge.artist.model.Artist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistControllerTest {

    @Mock
    ArtistService artistService;

    @InjectMocks
    ArtistController artistController;


    @Test
    @DisplayName("should find a artist by searching")
    public void searchArtistByName() {
        var searchTerm = "search";
        when(artistService.searchForArtist(searchTerm)).thenReturn(List.of());
        var response = artistController.searchArtistByName(searchTerm);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(response.getBody()).hasSize(0);
    }

    @Test
    @DisplayName("should get an artist by id")
    public void getArtistById() {
        var id = 1L;
        var artist = Artist.builder().id(id).name("test").build();
        when(artistService.getArtistById(id)).thenReturn(artist);
        var response = artistController.getArtistById(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(artist);
    }

    @Test
    @DisplayName("should create a new artist")
    public void createNewArtist() {
        var id = 1L;
        var artist = Artist.builder().id(id).name("test").build();
        when(artistService.createNewArtist(artist)).thenReturn(artist);
        var response = artistController.createNewArtist(artist);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(artist);
    }

    @Test
    @DisplayName("should create a new artist")
    public void updateArtist() {
        var id = 1L;
        var artist = Artist.builder().id(id).name("test").build();
        when(artistService.updateArtist(id, artist)).thenReturn(artist);
        var response = artistController.updateArtist(id, artist);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(artist);
    }

    @Test
    @DisplayName("should create a new artist")
    public void deleteArtist() {
        var response = artistController.deleteArtist(1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    }

}