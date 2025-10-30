package com.project.mvprecommender.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "fixtures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fixture {

    @Id
    @JsonProperty
    private long id;

    @JsonProperty("event")
    private Integer gameWeek;

    @JsonProperty("team_h")
    private Integer teamHome;

    @JsonProperty("team_a")
    private Integer teamAway;

    @JsonProperty("team_h_difficulty")
    private Integer teamHomeDifficulty;

    @JsonProperty("team_a_difficulty")
    private Integer teamAwayDifficulty;

    @JsonProperty("kickoff_time")
    private String kickoffTime;

    @JsonProperty("started")
    private Boolean started;

    @JsonProperty("finished")
    private Boolean finished;
}
