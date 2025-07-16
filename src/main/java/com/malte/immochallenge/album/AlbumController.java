package com.malte.immochallenge.album;

import com.malte.immochallenge.album.exceptions.AlbumAlreadyExistsException;
import com.malte.immochallenge.album.exceptions.AlbumNotFoundException;
import com.malte.immochallenge.album.exceptions.UpdateAlbumException;
import com.malte.immochallenge.album.model.Album;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("album")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @GetMapping("search/{searchTerm}")
    public ResponseEntity<List<Album>> searchAlbumByName(@PathVariable String searchTerm) {
        return ResponseEntity.ok(albumService.searchForAlbum(searchTerm));
    }

    @GetMapping("artist/{id}")
    public ResponseEntity<List<Album>> getAlbumsByArtist(@PathVariable long id) {
        return ResponseEntity.ok(albumService.getAlbumsByArtist(id));
    }

    @GetMapping("{id}")
    public ResponseEntity<Album> getAlbumById(@PathVariable long id) {
        return ResponseEntity.ok(albumService.getAlbumById(id));
    }

    @PostMapping
    public ResponseEntity<Album> createNewAlbum(@RequestBody Album album) {
        return ResponseEntity.ok(albumService.createNewAlbum(album));
    }

    @PatchMapping("{id}")
    public ResponseEntity<Album> updateAlbum(@PathVariable long id, @RequestBody Album album) {
        return ResponseEntity.ok(albumService.updateAlbum(id, album));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable long id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AlbumNotFoundException.class)
    public ResponseEntity<String> handleAlbumNotFound(AlbumNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UpdateAlbumException.class)
    public ResponseEntity<String> handleBadRequest(UpdateAlbumException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(AlbumAlreadyExistsException.class)
    public ResponseEntity<String> handleAlbumAlreadyExists(AlbumAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }
}
