package at.ac.uibk.swa.repositories;

import at.ac.uibk.swa.models.Person;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends CrudRepository<Person, UUID> {
    @Override
    List<Person> findAll();

    Optional<Person> findByToken(UUID token);

    Optional<Person> findByUsername(String username);

    @Transactional
    @Modifying
    @Query("update Person p set p.token = :token where p.id = :id")
    int updateToken(@Param("id") UUID id, @Param("token") UUID token);
}