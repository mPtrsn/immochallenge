package com.malte.immochallenge.artist.exceptions;

public class ArtistAlreadyExistsException extends RuntimeException {
    public ArtistAlreadyExistsException(String id) {
        super("artist with spotifyId " + id + " already exists");
    }
}
