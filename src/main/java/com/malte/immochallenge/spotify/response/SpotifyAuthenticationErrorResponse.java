package com.malte.immochallenge.spotify.response;

import lombok.Data;

@Data
public class SpotifyAuthenticationErrorResponse {
    String error;
    String error_description;
}
