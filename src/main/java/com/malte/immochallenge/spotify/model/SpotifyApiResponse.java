package com.malte.immochallenge.spotify.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class SpotifyApiResponse {
    List<SpotifyArtist> artists;
    List<SpotifyAlbum> albums;


}
