package com.malte.immochallenge.spotify.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpotifyApiErrorResponse {
    ErrorBody error;

    @Data
    public static class ErrorBody {
        int status;
        String message;
    }
}
