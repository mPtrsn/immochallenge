package com.malte.immochallenge.artist;


import com.malte.immochallenge.artist.exceptions.ArtistAlreadyExistsException;
import com.malte.immochallenge.artist.exceptions.ArtistNotFoundException;
import com.malte.immochallenge.artist.exceptions.UpdateArtistException;
import com.malte.immochallenge.artist.model.Artist;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("artist")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;


    @GetMapping("search/{searchTerm}")
    public ResponseEntity<List<Artist>> searchArtistByName(@PathVariable String searchTerm) {
        return ResponseEntity.ok(artistService.searchForArtist(searchTerm));
    }

    @GetMapping("{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable long id) {
        return ResponseEntity.ok(artistService.getArtistById(id));
    }

    @PostMapping
    public ResponseEntity<Artist> createNewArtist(@RequestBody Artist artist) {
        return ResponseEntity.ok(artistService.createNewArtist(artist));
    }

    @PatchMapping("{id}")
    public ResponseEntity<Artist> updateArtist(@PathVariable long id, @RequestBody Artist artist) {
        return ResponseEntity.ok(artistService.updateArtist(id, artist));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable long id) {
        artistService.deleteArtist(id);
        return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ArtistNotFoundException.class)
    public ResponseEntity<String> handleArtistNotFound(ArtistNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UpdateArtistException.class)
    public ResponseEntity<String> handleBadRequest(UpdateArtistException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ArtistAlreadyExistsException.class)
    public ResponseEntity<String> handleArtistAlreadyExists(ArtistAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }
}
