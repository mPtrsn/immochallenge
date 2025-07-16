package com.malte.immochallenge.album.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class SimplifiedArtist {
    String spotifyId;
    String name;
    String type;

    String href;
    String spotifyUri;
    String externalUrl;
}
