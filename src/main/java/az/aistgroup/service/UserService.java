package az.aistgroup.service;

import az.aistgroup.domain.dto.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserViewDto> getAllUsers();

    UserDto getUserByUsername(String username);

    boolean doesUserExist(String username);

    UserDto registerUser(RegisterDto registerDto);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(String username, UpdateUserRequestDto userDto);

    UserDto updateUserByAdmin(String username, UserDto userDto);

    void deleteUser(String username);

    void checkLoginCredentials(LoginDto loginDto);
}
