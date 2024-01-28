package az.aistgroup.domain.dto;

import az.aistgroup.domain.entity.User;
import az.aistgroup.util.Strings;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static az.aistgroup.constants.ValidationConstant.PASSWORD_MAX_LENGTH;
import static az.aistgroup.constants.ValidationConstant.PASSWORD_MIN_LENGTH;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements Serializable {
    @NotBlank(message = "{field.notBlank}")
    private String firstName;

    @NotBlank(message = "{field.notBlank}")
    private String lastName;

    @NotBlank(message = "{field.notBlank}")
    private String fatherName;

    @NotBlank(message = "{field.notBlank}")
    private String username;

    @NotBlank(message = "{field.notBlank}")
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull(message = "{field.notNull}")
    @Positive(message = "{field.positive}")
    @Digits(integer = 10, fraction = 2, message = "Amount format must be: e.g 34.75")
    private BigDecimal balance;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<String> authorities = new HashSet<>();

    public UserDto() {
    }

    public UserDto(User user) {
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setFatherName(user.getFatherName());
        setUsername(user.getUsername());
        setBalance(user.getBalance());
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (Strings.isNullOrEmpty(firstName)) {
            throw new IllegalArgumentException("firstName can not be null or empty!");
        }
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (Strings.isNullOrEmpty(lastName)) {
            throw new IllegalArgumentException("lastName can not be null or empty!");
        }
        this.lastName = lastName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        if (Strings.isNullOrEmpty(fatherName)) {
            throw new IllegalArgumentException("fatherName can not be null or empty!");
        }
        this.fatherName = fatherName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("username can not be null or empty!");
        }
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

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDto userDto = (UserDto) o;

        return username.equals(userDto.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
