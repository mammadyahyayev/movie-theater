package az.aistgroup.repository;

import az.aistgroup.domain.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("select t from Ticket t where t.seat.id = :seatId and t.movieSession.id = :sessionId")
    Optional<Ticket> findTicketBySeatInCurrentSession(Long seatId, Long sessionId);
}
