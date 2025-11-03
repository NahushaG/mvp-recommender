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
@Table(name = "teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {
    @Id
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("short_name")
    private String shortName;

    @JsonProperty("strength")
    private Integer strength;

    @JsonProperty("strength_overall_home")
    private Integer strengthOverallHome;

    @JsonProperty("strength_overall_away")
    private Integer strengthOverallAway;

    @JsonProperty("strength_attack_home")
    private Integer strengthAttackHome;

    @JsonProperty("strength_attack_away")
    private Integer strengthAttackAway;

    @JsonProperty("strength_defence_home")
    private Integer strengthDefenceHome;

    @JsonProperty("strength_defence_away")
    private Integer strengthDefenceAway;
}

