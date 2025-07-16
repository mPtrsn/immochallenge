package com.malte.immochallenge.album;

import com.malte.immochallenge.album.model.Album;
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
class AlbumControllerTest {

    @Mock
    AlbumService albumService;

    @InjectMocks
    AlbumController albumController;

    @Test
    void searchAlbumByName() {
        var searchTerm = "search";
        when(albumService.searchForAlbum(searchTerm)).thenReturn(List.of());
        var response = albumController.searchAlbumByName(searchTerm);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(response.getBody()).hasSize(0);
    }

    @Test
    void getAlbumsByArtist() {
        var artistId = 1;
        when(albumService.getAlbumsByArtist(artistId)).thenReturn(List.of());
        var response = albumController.getAlbumsByArtist(artistId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(0);
    }

    @Test
    void getAlbumById() {
        var id = 1L;
        var album = Album.builder().id(id).name("test").build();
        when(albumService.getAlbumById(id)).thenReturn(album);
        var response = albumController.getAlbumById(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(album);
    }

    @Test
    void createNewAlbum() {
        var id = 1L;
        var album = Album.builder().id(id).name("test").build();
        when(albumService.createNewAlbum(album)).thenReturn(album);
        var response = albumController.createNewAlbum(album);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(album);
    }

    @Test
    void updateAlbum() {
        var id = 1L;
        var album = Album.builder().id(id).name("test").build();
        when(albumService.updateAlbum(id, album)).thenReturn(album);
        var response = albumController.updateAlbum(id, album);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(album);
    }

    @Test
    void deleteAlbum() {
        var response = albumController.deleteAlbum(1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    }
}