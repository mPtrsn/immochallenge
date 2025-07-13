package com.malte.immochallenge.artist.model;

import com.malte.immochallenge.artist.repository.GenreConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * the date time when this artist was last manually modified
     */
    LocalDateTime lastModified;
    /**
     * the date where this artist was last synchronized with the data from the api
     */
    LocalDateTime lastSynchronized;

    String spotifyId;
    String href;
    String spotifyUri;
    String externalUrl;
    String name;
    long followers;
    @Convert(converter = GenreConverter.class)
    List<String> genres;
    int popularity;
    @ElementCollection
    List<ArtistImage> artistImages;


}
