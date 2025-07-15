package com.malte.immochallenge.spotify.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpotifyApiErrorResponse {
    ErrorBody error;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ErrorBody {
        int status;
        String message;
    }
}
