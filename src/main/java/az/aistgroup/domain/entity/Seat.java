package az.aistgroup.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
