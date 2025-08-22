package com.eze_dev.torneos.repository;

import com.eze_dev.torneos.model.Player;
import com.eze_dev.torneos.types.CategoryType;
import com.eze_dev.torneos.types.GenderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {

    @Query("""
    SELECT DISTINCT p FROM Player p
    WHERE EXISTS (
        SELECT 1 FROM Tournament t
        JOIN t.pairs pair
        WHERE t.categoryType = :category
        AND t.genderType = :gender
        AND (pair.player1.id = p.id OR pair.player2.id = p.id)
    )
    """)
    List<Player> findPlayersWhoPlayedInCategoryAndGender(
            @Param("category") CategoryType category,
            @Param("gender") GenderType gender
    );

    @Query("""
    SELECT DISTINCT p FROM Player p
    WHERE EXISTS (
        SELECT 1 FROM Tournament t
        JOIN t.pairs pair
        WHERE t.categoryType = :category
        AND t.genderType = :gender
        AND (pair.player1.id = p.id OR pair.player2.id = p.id)
    )
    ORDER BY p.lastName, p.name
    """)
    Page<Player> findPlayersWhoPlayedInCategoryAndGender(
            @Param("category") CategoryType category,
            @Param("gender") GenderType gender,
            Pageable pageable
    );

    boolean existsByDni(String dni);
}