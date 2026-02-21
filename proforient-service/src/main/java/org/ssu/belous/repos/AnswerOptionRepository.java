package org.ssu.belous.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssu.belous.models.AnswerOption;

import java.util.UUID;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, UUID> {}