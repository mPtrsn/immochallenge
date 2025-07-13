package com.malte.immochallenge.mapper;

import com.malte.immochallenge.artist.model.Artist;
import com.malte.immochallenge.artist.model.ArtistImage;
import com.malte.immochallenge.spotify.response.SpotifyArtist;
import com.malte.immochallenge.spotify.response.SpotifyImage;

public class SpotifyArtistMapper {
    public static Artist artistFromSpotify(SpotifyArtist spotifyArtist) {
        return Artist.builder()
                .spotifyId(spotifyArtist.getId())
                .href(spotifyArtist.getHref())
                .spotifyUri(spotifyArtist.getUri())
                .externalUrl(spotifyArtist.getExternal_urls().getSpotify())
                .name(spotifyArtist.getName())
                .followers(spotifyArtist.getFollowers().getTotal())
                .genres(spotifyArtist.getGenres())
                .popularity(spotifyArtist.getPopularity())
                .artistImages(spotifyArtist.getImages().stream().map(SpotifyArtistMapper::imageFromSpotify).toList())
                .build();
    }

    public static ArtistImage imageFromSpotify(SpotifyImage spotifyImage){
        return ArtistImage.builder()
                .url(spotifyImage.getUrl())
                .width(spotifyImage.getWidth())
                .height(spotifyImage.getHeight())
                .build();
    }
}



