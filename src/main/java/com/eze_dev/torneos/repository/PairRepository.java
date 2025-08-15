package com.eze_dev.torneos.repository;

import com.eze_dev.torneos.model.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PairRepository extends JpaRepository<Pair, UUID> {

    Optional<Pair> findByTeamName(String teamName);
    List<Pair> findByPlayer1_IdOrPlayer2_Id(UUID playerId1, UUID playerId2);
}
