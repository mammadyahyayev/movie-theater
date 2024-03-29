package az.aistgroup.domain.entity;

import az.aistgroup.security.AuthorityConstant;
import az.aistgroup.util.Strings;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

@Entity
@Table(name = "users")
public class User extends AbstractAuditingEntity<Long> {
    public static final String DEFAULT_USER_BALANCE = "100";
    public static final String DEFAULT_USER_ROLE = AuthorityConstant.USER;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_seq", allocationSize = 1)
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

    public User() {
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName + " " + this.fatherName;
    }

    public void addAuthority(Authority authority) {
        if (authority == null || Strings.isNullOrEmpty(authority.getName())) return;

        authority.getUsers().add(this);
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

    //region Getters & Setters & Equals & HashCode & ToString
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

    public Set<Authority> getAuthorities() {
        return authorities;
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

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("firstName='" + firstName + "'")
                .add("lastName='" + lastName + "'")
                .add("fatherName='" + fatherName + "'")
                .add("username='" + username + "'")
                .add("balance=" + balance)
                .toString();
    }
    //endregion
}
