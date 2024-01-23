package az.aistgroup.domain.dto;

import az.aistgroup.domain.entity.User;
import az.aistgroup.util.Strings;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

import static az.aistgroup.constants.ValidationConstant.PASSWORD_MAX_LENGTH;
import static az.aistgroup.constants.ValidationConstant.PASSWORD_MIN_LENGTH;

public class UserDto implements Serializable {
    @NotNull
    @NotBlank
    private String firstName;

    @NotNull
    @NotBlank
    private String lastName;

    @NotNull
    @NotBlank
    private String fatherName;

    @NotNull
    @NotBlank
    private String username;

    @NotBlank
    @NotNull
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public UserDto() {
    }

    public UserDto(User user) {
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setFatherName(user.getFatherName());
        setUsername(user.getUsername());
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
