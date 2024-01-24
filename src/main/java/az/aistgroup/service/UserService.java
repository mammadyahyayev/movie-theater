package az.aistgroup.service;

import az.aistgroup.domain.dto.LoginDto;
import az.aistgroup.domain.dto.UserDto;
import az.aistgroup.domain.dto.UserViewDto;

import java.util.List;

public interface UserService {
    List<UserViewDto> getAllUsers();

    UserDto getUserByUsername(String username);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(String username, UserDto userDto);

    void deleteUser(String username);

    void checkLoginCredentials(LoginDto loginDto);
}
