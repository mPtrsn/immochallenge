package com.malte.immochallenge.spotify.exception;

import lombok.Getter;

@Getter
public class SpotifyAuthException extends RuntimeException {
    String error;
    String description;

    public SpotifyAuthException(String error, String description) {
        super(error + ": " + description);
        this.error = error;
        this.description = description;
    }
}
