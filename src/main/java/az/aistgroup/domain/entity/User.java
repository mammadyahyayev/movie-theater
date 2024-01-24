package az.aistgroup.domain.entity;

import az.aistgroup.util.Strings;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(length = 50)
    private String firstName;

    @Column(length = 50)
    private String lastName;

    @Column(length = 50)
    private String fatherName;

    @Column(length = 50, unique = true, nullable = false)
    private String username;

    @Column(name = "encoded_password", length = 100, nullable = false)
    private String password;

    private BigDecimal balance;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_authorities",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private Set<Authority> authorities = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY, mappedBy = "user",
            orphanRemoval = true, cascade = CascadeType.ALL
    )
    private Set<Ticket> tickets = new HashSet<>();

    public User() {
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName + " " + this.fatherName;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void addAuthority(Authority authority) {
        if (authority == null || Strings.isNullOrEmpty(authority.getName())) return;

        this.authorities.add(authority);
    }

    public void increaseBalance(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount can not be null!");

        this.balance = this.balance.add(amount);
    }

    public void decreaseBalance(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount can not be null!");

        this.balance = this.balance.subtract(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }
}
