package com.malte.immochallenge;

import com.malte.immochallenge.album.AlbumService;
import com.malte.immochallenge.artist.ArtistService;
import com.malte.immochallenge.mapper.SpotifyApiMapper;
import com.malte.immochallenge.spotify.SpotifyService;
import com.malte.immochallenge.spotify.exception.SpotifyApiException;
import com.malte.immochallenge.spotify.exception.SpotifyAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@RequiredArgsConstructor
public class DataFetchingService {
    private final SpotifyService spotifyService;
    private final ArtistService artistService;
    private final AlbumService albumService;

    //    @Scheduled(cron = "0/60 0 0 ? * *")
    @Scheduled(fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
    public void getArtistDataPeriodically() {
        var synchronizationDate = LocalDateTime.now();
        try {
            var apiResponse = spotifyService.getDataFromSpotify();
            var mappedArtists = apiResponse.getArtists().stream()
                    .map(SpotifyApiMapper::artistFromSpotify)
                    .toList();
            var mappedAlbums = apiResponse.getAlbums().stream()
                    .map(SpotifyApiMapper::albumFromSpotify)
                    .toList();
            artistService.handleNewArtists(mappedArtists, synchronizationDate);
            albumService.handleNewAlbums(mappedAlbums, synchronizationDate);
        } catch (SpotifyApiException | SpotifyAuthException exception) {
            log.error(exception.getMessage());
        }
    }
}
