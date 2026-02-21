package org.ssu.belous.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "direction")
@Data
public class Direction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    @JsonBackReference
    private Faculty faculty;

    @ManyToMany(mappedBy = "directions")
    private List<Dimension> dimensions;
}
