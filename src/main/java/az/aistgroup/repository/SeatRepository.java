package az.aistgroup.repository;

import az.aistgroup.domain.entity.Seat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends CrudRepository<Seat, Long> {
}
