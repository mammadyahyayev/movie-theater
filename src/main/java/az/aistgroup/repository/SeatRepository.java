package az.aistgroup.repository;

import az.aistgroup.domain.dto.SeatDto;
import az.aistgroup.domain.entity.Seat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends CrudRepository<Seat, Long> {
    List<SeatDto> getAllSeats();
}
