package com.malte.immochallenge.album.exceptions;

public class AlbumAlreadyExistsException extends RuntimeException {
    public AlbumAlreadyExistsException(String spotifyId) {
        super("album with spotifyId " + spotifyId + " already exists");
    }
}
