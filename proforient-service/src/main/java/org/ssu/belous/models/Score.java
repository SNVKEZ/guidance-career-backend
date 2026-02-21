package org.ssu.belous.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "score")
@Data
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "value", nullable = false)
    private Integer value;

    @ManyToOne
    @JoinColumn(name = "dimension_id", nullable = false)
    private Dimension dimension;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private TestSession session;
}
