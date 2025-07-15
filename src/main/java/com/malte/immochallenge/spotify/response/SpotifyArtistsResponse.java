package com.malte.immochallenge.spotify.response;

import com.malte.immochallenge.spotify.model.SpotifyArtist;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SpotifyArtistsResponse {
    List<SpotifyArtist> artists;
}
