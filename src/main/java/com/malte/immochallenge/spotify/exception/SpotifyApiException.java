package com.malte.immochallenge.spotify.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SpotifyApiException extends RuntimeException {
    HttpStatus status;
    String errorMessage;

    public SpotifyApiException(HttpStatus status, String errorMessage) {
        super(status.toString() + ": " + errorMessage);
        this.status = status;
        this.errorMessage = errorMessage;
    }
}
