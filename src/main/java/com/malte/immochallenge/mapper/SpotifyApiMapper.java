package com.malte.immochallenge.mapper;

import com.malte.immochallenge.album.model.Album;
import com.malte.immochallenge.album.model.SimplifiedArtist;
import com.malte.immochallenge.artist.model.Artist;
import com.malte.immochallenge.artist.model.Image;
import com.malte.immochallenge.spotify.model.SpotifyAlbum;
import com.malte.immochallenge.spotify.model.SpotifyArtist;
import com.malte.immochallenge.spotify.model.SpotifyImage;

import java.util.Optional;

public class SpotifyApiMapper {

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
                .images(spotifyArtist.getImages().stream().map(SpotifyApiMapper::imageFromSpotify).toList())
                .build();
    }

    public static Album albumFromSpotify(SpotifyAlbum album) {
        return Album.builder()
                .spotifyId(album.getId())
                .spotifyUri(album.getUri())
                .name(album.getName())
                .albumType(album.getAlbum_type())
                .totalTracks(album.getTotal_tracks())
                .releaseDate(album.getRelease_date())
                .releaseDatePrecision(album.getRelease_date_precision())
                .albumGroup(album.getAlbum_group())
                .artists(album.getArtists().stream().map(SpotifyApiMapper::simpleArtistFromSpotify).toList())
                .restrictionReason(Optional.ofNullable(album.getRestrictions()).map(SpotifyAlbum.Restrictions::getReason).orElse(null))
                .images(album.getImages().stream().map(SpotifyApiMapper::imageFromSpotify).toList())
                .href(album.getHref())
                .spotifyUri(album.getUri())
                .externalUrl(album.getExternal_urls().getSpotify())
                .build();
    }

    private static SimplifiedArtist simpleArtistFromSpotify(SpotifyAlbum.SimplifiedArtist spotifyArtist) {
        return SimplifiedArtist.builder()
                .spotifyId(spotifyArtist.getId())
                .name(spotifyArtist.getName())
                .type(spotifyArtist.getType())
                .href(spotifyArtist.getHref())
                .spotifyUri(spotifyArtist.getUri())
                .externalUrl(spotifyArtist.getExternal_urls().getSpotify())
                .build();
    }

    private static Image imageFromSpotify(SpotifyImage spotifyImage) {
        return Image.builder()
                .url(spotifyImage.getUrl())
                .width(spotifyImage.getWidth())
                .height(spotifyImage.getHeight())
                .build();
    }
}



