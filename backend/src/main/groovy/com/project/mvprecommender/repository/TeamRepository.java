package com.project.mvprecommender.repository;

import com.project.mvprecommender.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    Team findByName(String name);
}
