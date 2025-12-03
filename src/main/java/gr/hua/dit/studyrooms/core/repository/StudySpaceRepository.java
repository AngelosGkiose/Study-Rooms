/*
Author: Sotirios Ilias
Student ID: it21925
*/

package gr.hua.dit.studyrooms.core.repository;

import gr.hua.dit.studyrooms.core.model.StudySpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudySpaceRepository extends JpaRepository<StudySpace, Long> {
    // custom queries will be added later (e.g. find by name)
}
