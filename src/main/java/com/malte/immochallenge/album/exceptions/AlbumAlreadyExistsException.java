package com.malte.immochallenge.album.exceptions;

public class AlbumAlreadyExistsException extends RuntimeException {
    public AlbumAlreadyExistsException(String spotifyId) {
        super("artist with spotify id " + spotifyId + " already exists");
    }
}
