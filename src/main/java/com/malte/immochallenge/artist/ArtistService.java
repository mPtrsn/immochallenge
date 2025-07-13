package com.malte.immochallenge.artist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final ArtistRepository artistRepository;

    public void createNewArtist() {

    }

    public Artist getArtistById(long id) {
        return artistRepository.findById(id).orElseThrow(RuntimeException::new); // TODO error handling
    }

    public List<Artist> searchForArtist(String searchTerm) {
        return artistRepository.findAllByFuzzyName(searchTerm);
    }

    public void updateArtist(long id, Artist newArtist) {

    }

    public void deleteArtist(long id) {

    }
}
