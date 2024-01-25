package az.aistgroup.domain.dto;

import jakarta.validation.constraints.NotBlank;


public class UpdateUserRequestDto {
    @NotBlank(message = "{field.notBlank}")
    private String firstName;

    @NotBlank(message = "{field.notBlank}")
    private String lastName;

    @NotBlank(message = "{field.notBlank}")
    private String fatherName;

    public UpdateUserRequestDto() {
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

}
