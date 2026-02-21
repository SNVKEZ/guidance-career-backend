package org.ssu.belous.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssu.belous.models.Questionnaire;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, UUID> {

    @Transactional
    Optional<Questionnaire> findByCode(String code);

    @Transactional
    boolean existsByCode(String code);

    @Transactional
    @Query("SELECT q FROM Questionnaire q LEFT JOIN FETCH q.questions ORDER BY q.title")
    List<Questionnaire> findAllWithQuestions();

    @Transactional
    @Query("""
        SELECT q FROM Questionnaire q
        LEFT JOIN FETCH q.questions
        WHERE q.code = :code
        """)
    Optional<Questionnaire> findByCodeWithQuestions(@Param("code") String code);

    @Modifying
    @Transactional
    @Query("DELETE FROM Questionnaire q WHERE q.code = :code")
    void deleteByCode(@Param("code") String code);
}