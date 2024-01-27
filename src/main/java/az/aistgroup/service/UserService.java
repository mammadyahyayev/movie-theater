package az.aistgroup.service;

import az.aistgroup.domain.dto.*;

import java.util.List;

public interface UserService {
    List<UserViewDto> getAllUsers();

    UserDto getUserByUsername(String username);

    boolean doesUserExist(String username);

    UserView registerUser(RegisterDto registerDto);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(String username, UpdateUserRequestDto userDto);

    UserDto updateUserByAdmin(String username, UserDto userDto);

    void deleteUser(String username);

    UserDto topUpBalance(TopUpBalanceDto topUpBalanceDto);

    void checkLoginCredentials(LoginDto loginDto);
}
