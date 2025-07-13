package com.malte.immochallenge.artist;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArtistRepository extends CrudRepository<Artist, Long> {

    @Query("""
            select a
            where a.name ILIKE '?1'
            """)
    List<Artist> findAllByFuzzyName(String searchTerm);
}
