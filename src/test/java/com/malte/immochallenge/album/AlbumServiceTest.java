package com.malte.immochallenge.album;

import com.malte.immochallenge.album.exceptions.AlbumAlreadyExistsException;
import com.malte.immochallenge.album.exceptions.AlbumNotFoundException;
import com.malte.immochallenge.album.exceptions.UpdateAlbumException;
import com.malte.immochallenge.album.model.Album;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    AlbumRepository albumRepository;

    @InjectMocks
    AlbumService albumService;

    @Nested
    public class GetAlbumById {
        @Test
        @DisplayName("get a album by id and ensure no modifications")
        public void getAlbumById2() {
            Album expected = getAlbum();

            when(albumRepository.findById(anyLong()))
                    .thenReturn(Optional.ofNullable(expected));

            Album album = albumService.getAlbumById(1L);

            assertThat(album).isEqualTo(expected);
        }

        @Test
        @DisplayName("should throw if album is not found")
        public void getAlbumById3() {
            when(albumRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> albumService.getAlbumById(1L)).isInstanceOf(AlbumNotFoundException.class)
                    .hasMessageContaining("Album with id 1 was not found");
        }
    }

    @Nested
    public class SearchForAlbum {
        @Test
        @DisplayName("search for album with no match")
        public void searchForAlbum1() {
            when(albumRepository.searchByName(anyString())).thenReturn(List.of());
            var searchResult = albumService.searchForAlbum("test");
            assertThat(searchResult).hasSize(0);
        }

        @Test
        @DisplayName("search for album with one match")
        public void searchForAlbum2() {
            when(albumRepository.searchByName(anyString())).thenReturn(List.of(Album.builder().build()));
            var searchResult = albumService.searchForAlbum("test");
            assertThat(searchResult).hasSize(1);
        }

        @Test
        @DisplayName("search for album with no match because search term is null")
        public void searchForAlbum3() {
            albumService.searchForAlbum(null);
            verify(albumRepository, times(0)).searchByName(anyString());
        }

        @Test
        @DisplayName("search for album with no match because search term is empty")
        public void searchForAlbum4() {
            albumService.searchForAlbum("");
            verify(albumRepository, times(0)).searchByName(anyString());
        }

        @Test
        @DisplayName("search for album with one match and ensure no modification")
        public void searchForAlbum5() {
            Album expected = getAlbum();

            when(albumRepository.searchByName(anyString()))
                    .thenReturn(List.of(expected));

            var result = albumService.searchForAlbum("test");

            assertThat(result).containsExactly(expected);
        }

        @ParameterizedTest
        @CsvSource({"test,%test%", "TEST,%test%", "1234,%1234%", "abc123, %abc123%", "a&/(;b c, %ab c%"})
        @DisplayName("check the input gets sanitized correctly")
        public void searchForAlbum6(String input, String sanitized) {
            when(albumRepository.searchByName(anyString()))
                    .thenReturn(List.of());
            albumService.searchForAlbum(input);
            verify(albumRepository).searchByName(sanitized);
        }
    }

    @Nested
    public class GetAlbumsByArtist {

        @Test
        @DisplayName("should get albums by artist id")
        public void getAlbumsByArtist() {
            when(albumRepository.getAlbumsByArtistId(1)).thenReturn(List.of(getAlbum()));
            var albums = albumService.getAlbumsByArtist(1);
            assertThat(albums).isNotNull();
            assertThat(albums).hasSize(1);
        }
    }

    @Nested
    public class HandleNewAlbums {
        LocalDateTime syncDate = LocalDateTime.now();

        @Test
        @DisplayName("Nothing should happen when no album is provided")
        public void handleNewAlbums1() {
            albumService.handleNewAlbums(List.of(), syncDate);
            verifyNoInteractions(albumRepository);
        }

        @Test
        @DisplayName("album should be inserted")
        public void handleNewAlbums2() {
            when(albumRepository.existsBySpotifyId(anyString())).thenReturn(false);

            albumService.handleNewAlbums(List.of(getAlbum()), syncDate);

            ArgumentCaptor<Album> albumCaptor = ArgumentCaptor.forClass(Album.class);
            verify(albumRepository).save(albumCaptor.capture());

            assertThat(albumCaptor.getAllValues()).hasSize(1);
            assertThat(albumCaptor.getValue().getLastSynchronized()).isEqualTo(syncDate);
        }

        @Test
        @DisplayName("album should be overridden")
        public void handleNewAlbums3() {
            Album album = getAlbum();
            Album existingAlbum = Album.builder()
                    .id(66L)
                    .lastSynchronized(syncDate.minusMinutes(13))
                    .spotifyId("existing")
                    .spotifyUri("existing")
                    .name("existing")
                    .albumType("existing")
                    .totalTracks(5)
                    .releaseDate("existing")
                    .releaseDatePrecision("existing")
                    .albumGroup("existing")
                    .artists(List.of())
                    .restrictionReason("existing")
                    .images(List.of(com.malte.immochallenge.model.Image.builder().url("existing").width(100).height(200).build()))
                    .href("existing")
                    .spotifyUri("existing")
                    .externalUrl("existing")
                    .build();
            album = album.toBuilder().lastSynchronized(syncDate.minusMinutes(5)).build();
            when(albumRepository.existsBySpotifyId(album.getSpotifyId())).thenReturn(true);
            when(albumRepository.albumWasModified(album.getSpotifyId())).thenReturn(false);
            when(albumRepository.findBySpotifyId(album.getSpotifyId())).thenReturn(Optional.of(existingAlbum));

            albumService.handleNewAlbums(List.of(album), syncDate);

            ArgumentCaptor<Album> albumCaptor = ArgumentCaptor.forClass(Album.class);
            verify(albumRepository).save(albumCaptor.capture());

            assertThat(albumCaptor.getAllValues()).hasSize(1);
            Album savedAlbum = albumCaptor.getValue();
            assertThat(savedAlbum.getLastSynchronized()).isEqualTo(syncDate);
            assertThat(savedAlbum.getId()).isEqualTo(existingAlbum.getId());
            assertThat(savedAlbum.getSpotifyId()).isEqualTo(existingAlbum.getSpotifyId());
            assertThat(savedAlbum).usingRecursiveComparison()
                    .ignoringFields("id", "spotifyId", "lastModified", "lastSynchronized")
                    .isEqualTo(album);
        }

        @Test
        @DisplayName("album should not be overridden")
        public void handleNewAlbums4() {
            Album album = getAlbum();
            album.setLastSynchronized(syncDate.minusMinutes(5));
            when(albumRepository.existsBySpotifyId(album.getSpotifyId())).thenReturn(true);
            when(albumRepository.albumWasModified(album.getSpotifyId())).thenReturn(true);

            albumService.handleNewAlbums(List.of(album), syncDate);

            verify(albumRepository, never()).save(any(Album.class));
        }

        @Test
        @DisplayName("should throw if user cant be found")
        public void handleNewAlbums5() {
            Album album = getAlbum();
            album.setLastSynchronized(syncDate.minusMinutes(5));
            when(albumRepository.existsBySpotifyId(album.getSpotifyId())).thenReturn(true);
            when(albumRepository.albumWasModified(album.getSpotifyId())).thenReturn(false);
            when(albumRepository.findBySpotifyId(album.getSpotifyId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> albumService.handleNewAlbums(List.of(album), syncDate)).isInstanceOf(AlbumNotFoundException.class)
                    .hasMessageContaining("Album with spotifyId spotifyId was not found");
        }
    }

    @Nested
    public class CreateNewAlbum {
        @Test
        @DisplayName("create New Album")
        public void createNewAlbum1() {
            Album album = getAlbum();
            when(albumRepository.existsBySpotifyId(album.getSpotifyId())).thenReturn(false);
            when(albumRepository.save(any(Album.class))).thenAnswer(i -> i.getArgument(0));

            Album savedAlbum = albumService.createNewAlbum(album);

            assertThat(savedAlbum.getId()).isNull();
            assertThat(savedAlbum).usingRecursiveComparison()
                    .ignoringFields("id", "lastModified")
                    .isEqualTo(album);
        }

        @Test
        @DisplayName("create New Album should throw when user exists")
        public void createNewAlbum2() {
            Album album = getAlbum();
            when(albumRepository.existsBySpotifyId(album.getSpotifyId())).thenReturn(true);

            assertThatThrownBy(() -> albumService.createNewAlbum(album)).isInstanceOf(AlbumAlreadyExistsException.class)
                    .hasMessageContaining("album with spotifyId spotifyId already exists");
        }
    }

    @Nested
    public class UpdateAlbum {
        @Test
        @DisplayName("update album")
        public void updateAlbum1() {
            Album album = getAlbum();
            when(albumRepository.existsById(album.getId())).thenReturn(true);
            when(albumRepository.save(any(Album.class))).thenAnswer(i -> i.getArgument(0));

            Album savedAlbum = albumService.updateAlbum(album.getId(), album);

            assertThat(savedAlbum.getLastModified()).isNotEqualTo(album.getLastModified());
            assertThat(savedAlbum).usingRecursiveComparison()
                    .ignoringFields("lastModified")
                    .isEqualTo(album);
        }

        @Test
        @DisplayName("update album should throw when user not exists")
        public void updateAlbum2() {
            Album album = getAlbum();
            when(albumRepository.existsById(album.getId())).thenReturn(false);

            assertThatThrownBy(() -> albumService.updateAlbum(album.getId(), album)).isInstanceOf(AlbumNotFoundException.class)
                    .hasMessageContaining("Album with id 1 was not found");
        }

        @Test
        @DisplayName("update album should throw when user not exists")
        public void updateAlbum3() {
            Album album = getAlbum();

            assertThatThrownBy(() -> albumService.updateAlbum(album.getId() + 1, album)).isInstanceOf(UpdateAlbumException.class)
                    .hasMessageContaining("provided id does not match provided album");
        }
    }

    @Nested
    public class DeleteAlbum {
        @Test
        @DisplayName("delete album")
        public void deleteAlbum1() {
            Album album = getAlbum();
            when(albumRepository.existsById(album.getId())).thenReturn(true);

            albumService.deleteAlbum(album.getId());
        }

        @Test
        @DisplayName("update album should throw when user not exists")
        public void deleteAlbum2() {
            Album album = getAlbum();
            when(albumRepository.existsById(album.getId())).thenReturn(false);

            assertThatThrownBy(() -> albumService.deleteAlbum(album.getId())).isInstanceOf(AlbumNotFoundException.class)
                    .hasMessageContaining("Album with id 1 was not found");
        }
    }

    private Album getAlbum() {
        return Album.builder()
                .id(1L)
                .lastModified(LocalDateTime.of(2025, 7, 7, 7, 7))
                .lastSynchronized(LocalDateTime.of(2025, 9, 9, 9, 9))
                .spotifyId("spotifyId")
                .name("name")
                .albumType("type")
                .totalTracks(5)
                .releaseDate("releaseDate")
                .releaseDatePrecision("precision")
                .albumGroup("albumGroup")
                .artists(List.of())
                .restrictionReason(null)
                .images(List.of(com.malte.immochallenge.model.Image.builder().url("image url").width(100).height(200).build()))
                .href("href")
                .spotifyUri("uri")
                .externalUrl("externalUrl")
                .build();
    }

}