package com.malte.immochallenge.spotify.response;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class SpotifyAccessTokenResponse {
    String access_token;
    String token_type;
    long expires_in;
}