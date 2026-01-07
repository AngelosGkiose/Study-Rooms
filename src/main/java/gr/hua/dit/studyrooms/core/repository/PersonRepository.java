package gr.hua.dit.studyrooms.core.repository;

import gr.hua.dit.studyrooms.core.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link Person} entity.
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {


    Optional<Person> findByEmailAddressIgnoreCase(final String emailAddress);

    boolean existsByEmailAddressIgnoreCase(final String emailAddress);

    boolean existsByMobilePhoneNumber(final String mobilePhoneNumber);

    boolean existsByStudentIdIgnoreCase(final String studentId);

}