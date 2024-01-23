package az.aistgroup.repository;

import az.aistgroup.domain.dto.HallDto;
import az.aistgroup.domain.entity.Hall;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HallRepository extends CrudRepository<Hall, Long> {
    List<HallDto> getAllHalls();
}
