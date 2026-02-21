package org.ssu.belous.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dimension")
@Data
public class Dimension {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT", length = 1024)
    private String description;

    @ManyToOne
    @JoinColumn(name = "questionnaire_id", nullable = false)
    private Questionnaire questionnaire;

    @ManyToMany
    @JoinTable(
            name = "dimension_faculties",
            joinColumns = @JoinColumn(name = "dimension_id"),
            inverseJoinColumns = @JoinColumn(name = "faculty_id")
    )
    private List<Faculty> faculties;

    @ManyToMany
    @JoinTable(
            name = "dimension_directions",
            joinColumns = @JoinColumn(name = "dimension_id"),
            inverseJoinColumns = @JoinColumn(name = "direction_id")
    )
    private List<Direction> directions;
}
