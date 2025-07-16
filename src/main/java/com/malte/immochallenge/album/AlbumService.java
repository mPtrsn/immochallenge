package com.malte.immochallenge.album;

import com.malte.immochallenge.album.exceptions.AlbumAlreadyExistsException;
import com.malte.immochallenge.album.exceptions.AlbumNotFoundException;
import com.malte.immochallenge.album.exceptions.UpdateAlbumException;
import com.malte.immochallenge.album.model.Album;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;


    public Album getAlbumById(long id) {
        return albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundException(id));
    }

    public List<Album> searchForAlbum(String searchTerm) {
        var sanitizedInput = sanitizeSearchInput(searchTerm);
        if (sanitizedInput == null) {
            return emptyList();
        }
        return albumRepository.searchByName("%" + sanitizedInput + "%");
    }

    public List<Album> getAlbumsByArtist(long artistId) {
        return albumRepository.getAlbumsByArtistId(artistId);
    }

    public void handleNewAlbums(List<Album> albums, LocalDateTime synchronizationDate) {
        for (Album album : albums) {
            var spotifyId = album.getSpotifyId();
            if (albumRepository.existsBySpotifyId(spotifyId)) {
                // album exists
                if (!albumRepository.albumWasModified(spotifyId)) {
                    // album was never modified
                    Album existingAlbum = albumRepository.findBySpotifyId(spotifyId)
                            .orElseThrow(() -> new AlbumNotFoundException(spotifyId));
                    album = existingAlbum.toBuilder()
                            .lastSynchronized(synchronizationDate)
                            .spotifyUri(album.getSpotifyUri())
                            .name(album.getName())
                            .albumType(album.getAlbumType())
                            .totalTracks(album.getTotalTracks())
                            .releaseDate(album.getReleaseDate())
                            .releaseDatePrecision(album.getReleaseDatePrecision())
                            .albumGroup(album.getAlbumGroup())
                            .artists(album.getArtists())
                            .restrictionReason(album.getRestrictionReason())
                            .images(album.getImages())
                            .href(album.getHref())
                            .spotifyUri(album.getSpotifyUri())
                            .externalUrl(album.getExternalUrl())
                            .build();
                    albumRepository.save(album);
                } else {
                    log.debug("album: {} was not updated because they were modified", album.getName());
                }
            } else {
                album.toBuilder().lastSynchronized(synchronizationDate).build();
                albumRepository.save(album);
            }
        }
    }


    public Album createNewAlbum(Album album) {
        album = album.toBuilder()
                .id(null)
                .lastModified(LocalDateTime.now())
                .build();
        if (!albumRepository.existsBySpotifyId(album.getSpotifyId())) {
            return albumRepository.save(album);
        } else {
            throw new AlbumAlreadyExistsException(album.getSpotifyId());
        }
    }

    public Album updateAlbum(long id, Album newAlbum) {
        if (newAlbum.getId() != id) {
            throw new UpdateAlbumException("provided id does not match provided album");
        }
        if (albumRepository.existsById(id)) {
            var albumToSave = newAlbum.toBuilder().lastModified(LocalDateTime.now()).build();
            return albumRepository.save(albumToSave);
        } else {
            throw new AlbumNotFoundException(id);
        }
    }

    public void deleteAlbum(long id) {
        if (albumRepository.existsById(id)) {
            albumRepository.deleteById(id);
        } else {
            throw new AlbumNotFoundException(id);
        }
    }


    private String sanitizeSearchInput(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return null;
        }
        return searchTerm.toLowerCase().replaceAll("[^a-zA-Z0-9-\\s]", "");
    }


}
