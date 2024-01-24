package az.aistgroup.controller;

import az.aistgroup.domain.dto.OperationResponseDto;
import az.aistgroup.domain.dto.UserDto;
import az.aistgroup.security.AuthorityConstant;
import az.aistgroup.security.SecurityUtils;
import az.aistgroup.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority(\"" + AuthorityConstant.ADMIN + "\")")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("username") String username) {
        checkUserHasPermission(username);

        UserDto user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority(\"" + AuthorityConstant.ADMIN + "\")")
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto userDto) {
        UserDto user = userService.addUser(userDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("username") String username, @Valid @RequestBody UserDto userDto) {
        checkUserHasPermission(username);

        UserDto user = userService.updateUser(username, userDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<OperationResponseDto> deleteUser(@PathVariable("username") String username) {
        checkUserHasPermission(username);

        userService.deleteUser(username);
        var response = new OperationResponseDto(true, "User with " + username + " deleted...");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void checkUserHasPermission(String username) {
        if (SecurityUtils.isSameLoggedInUser(username)) {
            return;
        }

        throw new AccessDeniedException("You don't have permission to access the resource!");
    }
}
