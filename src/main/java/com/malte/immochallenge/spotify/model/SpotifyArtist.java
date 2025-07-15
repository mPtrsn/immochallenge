package com.malte.immochallenge.spotify.model;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
