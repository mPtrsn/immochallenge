package com.malte.immochallenge.spotify.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SpotifyApiException extends RuntimeException {
    HttpStatus status;
    String message;

    public SpotifyApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
