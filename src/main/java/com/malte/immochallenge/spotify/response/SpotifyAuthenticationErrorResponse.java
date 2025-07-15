package com.malte.immochallenge.spotify.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpotifyAuthenticationErrorResponse {
    String error;
    String error_description;
}
