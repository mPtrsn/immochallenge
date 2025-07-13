package com.malte.immochallenge.artist;

import com.malte.immochallenge.artist.exceptions.ArtistAlreadyExistsException;
import com.malte.immochallenge.artist.exceptions.ArtistNotFoundException;
import com.malte.immochallenge.artist.model.Artist;
import com.malte.immochallenge.artist.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        return artistRepository.findAllByFuzzyName("%" + sanitizedInput + "%");
    }

    public void handleNewArtists(List<Artist> artists, LocalDateTime synchronizationDate) {
        int insertCount = 0;
        int modifiedCount = 0;
        int synchonizedCount = 0;
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
                    synchonizedCount++;
                } else {
                    log.debug("artist: {} was not updated because they were modified", artist.getName());
                    modifiedCount++;
                }
            } else {
                artist.setLastSynchronized(synchronizationDate);
                artistRepository.save(artist);
                insertCount++;
            }
        }
        log.debug("got {} artists. {} were new, {} were synchronized, {} have been manually modified", artists.size(), insertCount, synchonizedCount, modifiedCount);
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
        if (artistRepository.existsById(id)) {
            newArtist.setLastModified(LocalDateTime.now());
            return artistRepository.save(newArtist);
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
        if (searchTerm == null) {
            return null;
        }
        return searchTerm.replaceAll("[^a-zA-Z0-9\\s]", "");
    }

}
