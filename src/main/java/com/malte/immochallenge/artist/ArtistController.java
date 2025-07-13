package com.malte.immochallenge.artist;


import com.malte.immochallenge.spotify.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("artist")
@RequiredArgsConstructor
public class ArtistController {

    private final SpotifyService spotifyService;

    @GetMapping("test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok(spotifyService.getAccessToken());
    }
}
