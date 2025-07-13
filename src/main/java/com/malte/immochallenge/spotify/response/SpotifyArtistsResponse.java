package com.malte.immochallenge.spotify.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SpotifyArtistsResponse {
    List<SpotifyArtist> artists;
}
