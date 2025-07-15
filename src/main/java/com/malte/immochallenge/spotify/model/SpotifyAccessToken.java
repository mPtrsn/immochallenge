package com.malte.immochallenge.spotify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpotifyAccessToken {
    String token;
    String tokenType;
    long expiresIn;
    LocalDateTime timestamp;
}
