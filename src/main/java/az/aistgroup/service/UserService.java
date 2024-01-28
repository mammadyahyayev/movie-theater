package az.aistgroup.service;

import az.aistgroup.domain.dto.*;

import java.util.List;

public interface UserService {
    List<UserView> getAllUsers();

    UserDto getUserByUsername(String username);

    boolean doesUserExist(String username);

    UserModelView registerUser(RegisterDto registerDto);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(String username, UpdateUserRequestDto userDto);

    void deleteUser(String username);

    UserDto topUpBalance(TopUpBalanceDto topUpBalanceDto);

    void checkLoginCredentials(LoginDto loginDto);
}
