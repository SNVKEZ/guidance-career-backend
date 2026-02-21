package org.ssu.belous.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.ssu.belous.models.Dimension;

import java.util.List;
import java.util.UUID;

public interface DimensionRepository extends JpaRepository<Dimension, UUID> {
    @Transactional
    List<Dimension> findAllByQuestionnaire_Id(UUID questionnaireId);
}
