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
public class SpotifyArtist {
    String id;
    String href;
    String uri;
    String name;
    String type;
    int popularity;

    SpotifyExternalUrls external_urls;
    SpotifyFollowers followers;
    List<String> genres;
    List<SpotifyImage> images;
}
