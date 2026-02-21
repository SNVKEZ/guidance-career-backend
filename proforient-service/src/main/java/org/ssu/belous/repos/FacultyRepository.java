package org.ssu.belous.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ssu.belous.models.Faculty;

import java.util.List;
import java.util.UUID;

public interface FacultyRepository extends JpaRepository<Faculty, UUID> {
    @Query("SELECT DISTINCT f FROM Faculty f LEFT JOIN FETCH f.directions ORDER BY f.name")
    List<Faculty> findAllWithDirections();
}