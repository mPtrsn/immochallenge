package com.malte.immochallenge.artist;

import com.malte.immochallenge.artist.exceptions.ArtistAlreadyExistsException;
import com.malte.immochallenge.artist.exceptions.ArtistNotFoundException;
import com.malte.immochallenge.artist.exceptions.UpdateArtistException;
import com.malte.immochallenge.artist.model.Artist;
import com.malte.immochallenge.artist.model.ArtistImage;
import com.malte.immochallenge.artist.repository.ArtistRepository;
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
class ArtistServiceTest {

    @Mock
    ArtistRepository artistRepository;

    @InjectMocks
    ArtistService artistService;

    @Nested
    public class GetArtistById {
        @Test
        @DisplayName("get artist by id")
        public void getArtistById1() {
            when(artistRepository.findById(anyLong())).thenReturn(Optional.ofNullable(Artist.builder().build()));

            Artist artist = artistService.getArtistById(1L);

            assertThat(artist).isNotNull();
        }

        @Test
        @DisplayName("get a artist by id and ensure no modifications")
        public void getArtistById2() {
            Artist expected = getArtist();

            when(artistRepository.findById(anyLong()))
                    .thenReturn(Optional.ofNullable(expected));

            Artist artist = artistService.getArtistById(1L);

            assertThat(artist).isEqualTo(expected);
        }

        @Test
        @DisplayName("should throw if artist is not found")
        public void getArtistById3() {
            when(artistRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> artistService.getArtistById(1L)).isInstanceOf(ArtistNotFoundException.class)
                    .hasMessageContaining("Artist with id 1 was not found");
        }
    }

    @Nested
    public class SearchForArtist {
        @Test
        @DisplayName("search for artist with no match")
        public void searchForArtist1() {
            when(artistRepository.searchByName(anyString())).thenReturn(List.of());
            var searchResult = artistService.searchForArtist("test");
            assertThat(searchResult).hasSize(0);
        }

        @Test
        @DisplayName("search for artist with one match")
        public void searchForArtist2() {
            when(artistRepository.searchByName(anyString())).thenReturn(List.of(Artist.builder().build()));
            var searchResult = artistService.searchForArtist("test");
            assertThat(searchResult).hasSize(1);
        }

        @Test
        @DisplayName("search for artist with no match because search term is null")
        public void searchForArtist3() {
            artistService.searchForArtist(null);
            verify(artistRepository, times(0)).searchByName(anyString());
        }

        @Test
        @DisplayName("search for artist with no match because search term is empty")
        public void searchForArtist4() {
            artistService.searchForArtist("");
            verify(artistRepository, times(0)).searchByName(anyString());
        }

        @Test
        @DisplayName("search for artist with one match and ensure no modification")
        public void searchForArtist5() {
            Artist expected = getArtist();

            when(artistRepository.searchByName(anyString()))
                    .thenReturn(List.of(expected));

            var result = artistService.searchForArtist("test");

            assertThat(result).containsExactly(expected);
        }

        @ParameterizedTest
        @CsvSource({"test,%test%", "TEST,%test%", "1234,%1234%", "abc123, %abc123%", "a&/(;b c, %ab c%"})
        @DisplayName("check the input gets sanitized correctly")
        public void searchForArtist6(String input, String sanitized) {
            when(artistRepository.searchByName(anyString()))
                    .thenReturn(List.of());
            artistService.searchForArtist(input);
            verify(artistRepository).searchByName(sanitized);
        }
    }

    @Nested
    public class HandleNewArtists {
        LocalDateTime syncDate = LocalDateTime.now();

        @Test
        @DisplayName("Nothing should happen when no user is provided")
        public void handleNewArtists1() {
            artistService.handleNewArtists(List.of(), syncDate);
            verifyNoInteractions(artistRepository);
        }

        @Test
        @DisplayName("artist should be inserted")
        public void handleNewArtists2() {
            when(artistRepository.existsBySpotifyId(anyString())).thenReturn(false);

            artistService.handleNewArtists(List.of(getArtist()), syncDate);

            ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);
            verify(artistRepository).save(artistCaptor.capture());

            assertThat(artistCaptor.getAllValues()).hasSize(1);
            assertThat(artistCaptor.getValue().getLastSynchronized()).isEqualTo(syncDate);
        }

        @Test
        @DisplayName("artist should be overridden")
        public void handleNewArtists3() {
            Artist artist = getArtist();
            Artist existingArtist = Artist.builder()
                    .id(99L)
                    .lastModified(LocalDateTime.now())
                    .lastSynchronized(syncDate.minusMinutes(10))
                    .href("existing")
                    .spotifyUri("existing")
                    .externalUrl("existing")
                    .name("existing")
                    .followers(1)
                    .genres(List.of("existing"))
                    .popularity(1)
                    .artistImages(List.of(ArtistImage.builder().url("existing").width(1).height(1).build()))
                    .build();
            artist.setLastSynchronized(syncDate.minusMinutes(5));
            when(artistRepository.existsBySpotifyId(artist.getSpotifyId())).thenReturn(true);
            when(artistRepository.artistWasModified(artist.getSpotifyId())).thenReturn(false);
            when(artistRepository.findBySpotifyId(artist.getSpotifyId())).thenReturn(Optional.of(existingArtist));

            artistService.handleNewArtists(List.of(artist), syncDate);

            ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);
            verify(artistRepository).save(artistCaptor.capture());

            assertThat(artistCaptor.getAllValues()).hasSize(1);
            Artist savedArtist = artistCaptor.getValue();
            assertThat(savedArtist.getLastSynchronized()).isEqualTo(syncDate);
            assertThat(savedArtist.getId()).isEqualTo(existingArtist.getId());
            assertThat(savedArtist.getSpotifyId()).isEqualTo(existingArtist.getSpotifyId());
            assertThat(savedArtist).usingRecursiveComparison()
                    .ignoringFields("id", "spotifyId","lastModified", "lastSynchronized")
                    .isEqualTo(artist);
        }

        @Test
        @DisplayName("artist should not be overridden")
        public void handleNewArtists4() {
            Artist artist = getArtist();
            artist.setLastSynchronized(syncDate.minusMinutes(5));
            when(artistRepository.existsBySpotifyId(artist.getSpotifyId())).thenReturn(true);
            when(artistRepository.artistWasModified(artist.getSpotifyId())).thenReturn(true);

            artistService.handleNewArtists(List.of(artist), syncDate);

            verify(artistRepository, never()).save(any(Artist.class));
        }
    }

    @Nested
    public class CreateNewArtist {
        @Test
        @DisplayName("create New Artist")
        public void createNewArtist1(){
            Artist artist = getArtist();
            when(artistRepository.existsBySpotifyId(artist.getSpotifyId())).thenReturn(false);
            when(artistRepository.save(any(Artist.class))).thenAnswer(i -> i.getArgument(0));

            Artist savedArtist = artistService.createNewArtist(artist);

            assertThat(savedArtist.getId()).isNull();
            assertThat(savedArtist).usingRecursiveComparison()
                    .ignoringFields("id","lastModified")
                    .isEqualTo(artist);
        }

        @Test
        @DisplayName("create New Artist should throw when user exists")
        public void createNewArtist2(){
            Artist artist = getArtist();
            when(artistRepository.existsBySpotifyId(artist.getSpotifyId())).thenReturn(true);

            assertThatThrownBy(() -> artistService.createNewArtist(artist)).isInstanceOf(ArtistAlreadyExistsException.class)
                    .hasMessageContaining("artist with spotify id spotifyId already exists");
        }
    }

    @Nested
    public class UpdateArtist {
        @Test
        @DisplayName("update artist")
        public void updateArtist1(){
            Artist artist = getArtist();
            when(artistRepository.existsById(artist.getId())).thenReturn(true);
            when(artistRepository.save(any(Artist.class))).thenAnswer(i -> i.getArgument(0));

            Artist savedArtist = artistService.updateArtist(artist.getId(),artist);

            assertThat(savedArtist.getLastModified()).isNotEqualTo(artist.getLastModified());
            assertThat(savedArtist).usingRecursiveComparison()
                    .ignoringFields("lastModified")
                    .isEqualTo(artist);
        }

        @Test
        @DisplayName("update artist should throw when user not exists")
        public void updateArtist2(){
            Artist artist = getArtist();
            when(artistRepository.existsById(artist.getId())).thenReturn(false);

            assertThatThrownBy(() -> artistService.updateArtist(artist.getId(), artist)).isInstanceOf(ArtistNotFoundException.class)
                    .hasMessageContaining("Artist with id 1 was not found");
        }

        @Test
        @DisplayName("update artist should throw when user not exists")
        public void updateArtist3(){
            Artist artist = getArtist();

            assertThatThrownBy(() -> artistService.updateArtist(artist.getId() + 1, artist)).isInstanceOf(UpdateArtistException.class)
                    .hasMessageContaining("provided id does not match provided artist");
        }
    }

    @Nested
    public class DeleteArtist {
        @Test
        @DisplayName("delete artist")
        public void deleteArtist1(){
            Artist artist = getArtist();
            when(artistRepository.existsById(artist.getId())).thenReturn(true);

            artistService.deleteArtist(artist.getId());
        }

        @Test
        @DisplayName("update artist should throw when user not exists")
        public void deleteArtist2(){
            Artist artist = getArtist();
            when(artistRepository.existsById(artist.getId())).thenReturn(false);

            assertThatThrownBy(() -> artistService.deleteArtist(artist.getId())).isInstanceOf(ArtistNotFoundException.class)
                    .hasMessageContaining("Artist with id 1 was not found");
        }
    }

    private static Artist getArtist() {

        return Artist.builder()
                .id(1L)
                .lastModified(LocalDateTime.of(2025,10,10,10,10,10,10))
                .lastSynchronized(LocalDateTime.of(2025,5,5,5,5,5,5))
                .spotifyId("spotifyId")
                .href("href")
                .spotifyUri("spotifyUri")
                .externalUrl("externalUrl")
                .name("name")
                .followers(123456L)
                .genres(List.of("genre1", "genre2"))
                .popularity(50)
                .artistImages(List.of(ArtistImage.builder().url("image url").width(100).height(200).build()))
                .build();
    }

}