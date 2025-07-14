package com.malte.immochallenge.artist.repository;

import com.malte.immochallenge.artist.model.Artist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository extends CrudRepository<Artist, Long> {

    @Query(value = """
                SELECT *
                FROM Artist a
                WHERE lower(a.name)
                ILIKE lower(:term)
            """, nativeQuery = true)
    List<Artist> searchByName(@Param("term") String searchTerm);

    @Query("""
            SELECT EXISTS (
                SELECT 1
                FROM Artist a
                WHERE a.spotifyId = ?1
            )
            """)
    boolean existsBySpotifyId(String spotifyId);

    @Query("""
            SELECT EXISTS (
                SELECT 1
                FROM Artist a
                WHERE a.spotifyId = ?1 AND lastModified IS NOT NULL
            )
            """)
    boolean artistWasModified(String spotifyId);

    Optional<Artist> findBySpotifyId(String spotifyId);
}

/*

@Query(value = """
                SELECT p.id AS id,
                       p.firebase_id AS firebaseId,
                       p.username AS username,
                       p.profile_image_url AS profileImageUrl
                FROM Profile p
                WHERE lower(p.username)
                ILIKE lower(:term)
            """, nativeQuery = true)
    List<ShortProfileProjection> findShortProfileByPartialUsername(@Param("term") String searchTerm);
 */