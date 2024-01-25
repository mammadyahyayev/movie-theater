package az.aistgroup.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static az.aistgroup.constants.ValidationConstant.PASSWORD_MAX_LENGTH;
import static az.aistgroup.constants.ValidationConstant.PASSWORD_MIN_LENGTH;

public class RegisterDto {
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

    public RegisterDto() {
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
}
