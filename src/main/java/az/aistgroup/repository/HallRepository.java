package az.aistgroup.repository;

import az.aistgroup.domain.entity.Hall;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HallRepository extends CrudRepository<Hall, Long> {
}
