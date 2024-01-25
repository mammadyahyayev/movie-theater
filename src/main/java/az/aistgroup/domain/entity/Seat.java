package az.aistgroup.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seat_generator")
    @SequenceGenerator(name = "seat_generator", sequenceName = "seat_seq", allocationSize = 1)
    private Long id;

    private int seatNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }
}
