package az.aistgroup.service;

import az.aistgroup.domain.dto.LoginDto;
import az.aistgroup.domain.dto.UserDto;
import org.springframework.stereotype.Service;

public interface UserService {
    UserDto addUser(UserDto userDto);

    void checkLoginCredentials(LoginDto loginDto);
}
