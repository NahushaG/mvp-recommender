package com.project.mvprecommender.repository;

import com.project.mvprecommender.model.Fixture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FixtureRepository extends JpaRepository<Fixture, Long> {

    List<Fixture> findByGameWeek(Integer gameweek);

    @Query("SELECT f FROM Fixture f WHERE f.gameWeek >= :startGw AND f.gameWeek <= :endGw AND (f.teamHome = :teamId OR f.teamAway = :teamId)")
    List<Fixture> findTeamFixtures(@Param("teamId") Integer teamId, @Param("startGw") Integer startGw, @Param("endGw") Integer endGw);

}
