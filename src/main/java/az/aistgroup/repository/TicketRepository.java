package az.aistgroup.repository;

import az.aistgroup.domain.dto.TicketDto;
import az.aistgroup.domain.entity.Ticket;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends CrudRepository<Ticket, Long> {
    List<TicketDto> getAllTickets();
}
