package com.malte.immochallenge.album.exceptions;

public class AlbumNotFoundException extends RuntimeException {
    public AlbumNotFoundException(String spotifyId) {
        super("Album with id " + spotifyId + " was not found");
    }

    public AlbumNotFoundException(long id) {
        super("Album with id " + id + " was not found");
    }
}
