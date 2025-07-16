package com.malte.immochallenge.spotify.response;

import com.malte.immochallenge.spotify.model.SpotifyAlbum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpotifyAlbumsResponse {
    String href;
    int limit;
    String next;
    int offset;
    String previous;
    int total;
    List<SpotifyAlbum> items;
}
