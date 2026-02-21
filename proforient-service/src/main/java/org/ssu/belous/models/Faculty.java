package org.ssu.belous.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "faculties")
@Data
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Direction> directions;

    @ManyToMany(mappedBy = "faculties")
    private List<Dimension> dimensions;

    @Column(name = "link", columnDefinition = "TEXT")
    private String link;
}