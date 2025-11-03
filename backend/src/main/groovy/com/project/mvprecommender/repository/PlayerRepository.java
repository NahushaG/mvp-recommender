package com.project.mvprecommender.repository;

import com.project.mvprecommender.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByPosition(Integer teamId);

    @Query("SELECT p FROM Player p WHERE p.position = :position ORDER BY p.totalPoints DESC")
    List<Player> findTopPlayersByPosition(@Param("position") Integer position);

    @Query("SELECT p FROM Player p WHERE p.status = 'i' OR p.status = 'u' OR p.status = 'd'")
    List<Player> findInjuredOrDoubtfulPlayers();

    @Query("SELECT p FROM Player p WHERE p.nowCost <= :maxCost AND p.position = :position ORDER BY p.totalPoints DESC")
    List<Player> findAffordablePlayersByPosition(@Param("maxCost") Integer maxCost, @Param("position") Integer position);

    @Query("SELECT p.id FROM Player p")
    Set<Long> getAllPlayerIds();
}
