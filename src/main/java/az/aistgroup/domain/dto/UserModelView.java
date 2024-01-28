package az.aistgroup.domain.dto;

import az.aistgroup.domain.entity.User;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

public class UserModelView extends RepresentationModel<UserModelView> {
    private String firstName;
    private String lastName;
    private String fatherName;
    private String username;
    private BigDecimal balance;

    public UserModelView() {
    }

    public UserModelView(User user) {
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
