package com.malte.immochallenge.spotify.model;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpotifyAlbum {
    String album_type;
    int total_tracks;
    List<String> available_markets;
    SpotifyExternalUrls external_urls;
    String href;
    String id;
    List<SpotifyImage> images;
    String name;
    String release_date;
    String release_date_precision;
    @Nullable
    Restrictions restrictions;
    String type;
    String uri;
    List<SimplifiedArtist> artists;
    String album_group;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Restrictions {
        String reason;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SimplifiedArtist {
        SpotifyExternalUrls external_urls;
        String href;
        String id;
        String name;
        String type;
        String uri;
    }
}
/*

{
      "album_type": "compilation",
      "total_tracks": 9,
      "available_markets": ["CA", "BR", "IT"],
      "external_urls": {
        "spotify": "string"
      },
      "href": "string",
      "id": "2up3OPMp9Tb4dAKM2erWXQ",
      "images": [
        {
          "url": "https://i.scdn.co/image/ab67616d00001e02ff9ca10b55ce82ae553c8228",
          "height": 300,
          "width": 300
        }
      ],
      "name": "string",
      "release_date": "1981-12",
      "release_date_precision": "year",
      "restrictions": {
        "reason": "market"
      },
      "type": "album",
      "uri": "spotify:album:2up3OPMp9Tb4dAKM2erWXQ",
      "artists": [
        {
          "external_urls": {
            "spotify": "string"
          },
          "href": "string",
          "id": "string",
          "name": "string",
          "type": "artist",
          "uri": "string"
        }
      ],
      "album_group": "compilation"
    }
 */