package com.malte.immochallenge.album;

import com.malte.immochallenge.album.model.Album;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends CrudRepository<Album, Long> {
    @Query(value = """
                SELECT *
                FROM Album a
                WHERE lower(a.name)
                ILIKE lower(:term)
            """, nativeQuery = true)
    List<Album> searchByName(@Param("term") String searchTerm);

    @Query("""
            SELECT EXISTS (
                SELECT 1
                FROM Album a
                WHERE a.spotifyId = ?1
            )
            """)
    boolean existsBySpotifyId(String spotifyId);

    @Query("""
            SELECT EXISTS (
                SELECT 1
                FROM Album a
                WHERE a.spotifyId = ?1 AND lastModified IS NOT NULL
            )
            """)
    boolean albumWasModified(String spotifyId);

    Optional<Album> findBySpotifyId(String spotifyId);

    @Query(value = """
            SELECT DISTINCT a.* FROM album a
            INNER JOIN album_artists aa ON a.id = aa.album_id
            INNER JOIN artist ar ON ar.spotify_id = aa.spotify_id
            WHERE ar.id = :artistId
            """, nativeQuery = true)
    List<Album> getAlbumsByArtistId(@Param("artistId") long artistId);
}
