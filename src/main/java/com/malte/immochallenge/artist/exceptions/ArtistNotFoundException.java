package com.malte.immochallenge.artist.exceptions;

public class ArtistNotFoundException extends RuntimeException {
    public ArtistNotFoundException(long id) {
        super("Artist with id " + id + " was not found");
    }

    public ArtistNotFoundException(String id) {
        super("Artist with spotify id " + id + " was not found");
    }
}
