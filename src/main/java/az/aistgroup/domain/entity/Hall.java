package az.aistgroup.domain.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "halls")
public class Hall extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hall_generator")
    @SequenceGenerator(name = "hall_generator", sequenceName = "hall_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true)
    private String name;

    private int capacity;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "hall", cascade = CascadeType.ALL)
    private Set<Seat> seats = new HashSet<>();

    //region Getters & Setters
    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Set<Seat> getSeats() {
        return seats;
    }

    public void setSeats(Set<Seat> seats) {
        this.seats = seats;
    }
    //endregion
}
