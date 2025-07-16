package com.malte.immochallenge.album.model;


import com.malte.immochallenge.artist.model.Image;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Album {
    @Id
    @GeneratedValue
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
    String name;
    String albumType;
    int totalTracks;
    String releaseDate;
    String releaseDatePrecision;
    String albumGroup;
    @ElementCollection
    List<SimplifiedArtist> artists;
    String restrictionReason;
    @ElementCollection
    List<Image> images;

    String href;
    String spotifyUri;
    String externalUrl;
}
