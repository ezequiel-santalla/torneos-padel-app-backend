package com.eze_dev.torneos.repository;

import com.eze_dev.torneos.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, UUID> {

    boolean existsByName(String name);

    @Query("SELECT DISTINCT t FROM Tournament t " +
            "JOIN t.pairs p " +
            "WHERE p.player1.id = :playerId OR p.player2.id = :playerId")
    List<Tournament> findTournamentsByPlayerId(@Param("playerId") UUID playerId);
}
