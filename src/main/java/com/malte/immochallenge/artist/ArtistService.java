package com.malte.immochallenge.artist;

import com.malte.immochallenge.artist.exceptions.ArtistAlreadyExistsException;
import com.malte.immochallenge.artist.exceptions.ArtistNotFoundException;
import com.malte.immochallenge.artist.exceptions.UpdateArtistException;
import com.malte.immochallenge.artist.model.Artist;
import com.malte.immochallenge.artist.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtistService {
    private final ArtistRepository artistRepository;

    public Artist getArtistById(long id) {
        return artistRepository.findById(id).orElseThrow(() -> new ArtistNotFoundException(id));
    }

    public List<Artist> searchForArtist(String searchTerm) {
        var sanitizedInput = sanitizeSearchInput(searchTerm);
        if (sanitizedInput == null) {
            return emptyList();
        }
        return artistRepository.searchByName("%" + sanitizedInput + "%");
    }

    public void handleNewArtists(List<Artist> artists, LocalDateTime synchronizationDate) {
        for (Artist artist : artists) {
            var spotifyId = artist.getSpotifyId();
            if (artistRepository.existsBySpotifyId(spotifyId)) {
                // artist exists
                if (!artistRepository.artistWasModified(spotifyId)) {
                    // artist was never modified
                    Artist existingArtist = artistRepository.findBySpotifyId(spotifyId).orElseThrow(() -> new ArtistNotFoundException(spotifyId));
                    artist = existingArtist.toBuilder()
                            .lastSynchronized(synchronizationDate)
                            .href(artist.getHref())
                            .spotifyUri(artist.getSpotifyUri())
                            .externalUrl(artist.getExternalUrl())
                            .name(artist.getName())
                            .followers(artist.getFollowers())
                            .genres(artist.getGenres())
                            .popularity(artist.getPopularity())
                            .artistImages(artist.getArtistImages())
                            .build();
                    artistRepository.save(artist);
                } else {
                    log.debug("artist: {} was not updated because they were modified", artist.getName());
                }
            } else {
                artist.setLastSynchronized(synchronizationDate);
                artistRepository.save(artist);
            }
        }
    }

    public Artist createNewArtist(Artist artist) {
        artist = artist.toBuilder()
                .id(null)
                .lastModified(LocalDateTime.now())
                .build();
        if (!artistRepository.existsBySpotifyId(artist.getSpotifyId())) {
            return artistRepository.save(artist);
        } else {
            throw new ArtistAlreadyExistsException(artist.getSpotifyId());
        }
    }

    public Artist updateArtist(long id, Artist newArtist) {
        if (newArtist.getId() != id) {
            throw new UpdateArtistException("provided id does not match provided artist");
        }
        if (artistRepository.existsById(id)) {
            var artistToSave = newArtist.toBuilder().lastModified(LocalDateTime.now()).build();
            return artistRepository.save(artistToSave);
        } else {
            throw new ArtistNotFoundException(id);
        }
    }

    public void deleteArtist(long id) {
        if (artistRepository.existsById(id)) {
            artistRepository.deleteById(id);
        } else {
            throw new ArtistNotFoundException(id);
        }
    }


    private String sanitizeSearchInput(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return null;
        }
        return searchTerm.toLowerCase().replaceAll("[^a-zA-Z0-9-\\s]", "");
    }

}
