package az.aistgroup.controller;

import az.aistgroup.domain.dto.OperationResponseDto;
import az.aistgroup.domain.dto.UserDto;
import az.aistgroup.domain.dto.UserViewDto;
import az.aistgroup.security.AuthorityConstant;
import az.aistgroup.security.SecurityUtils;
import az.aistgroup.service.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The controller is used to manage users.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Fetches all users. Only ADMIN can get all available users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns all available users.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserViewDto.class))})
    })
    @GetMapping
    @PreAuthorize("hasAuthority(\"" + AuthorityConstant.ADMIN + "\")")
    public ResponseEntity<List<UserViewDto>> getAllUsers() {
        List<UserViewDto> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Fetch a user by his username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return a user for given username.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(
                    responseCode = "403",
                    description = "Given username and logged in user's username aren't matched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))})
    })
    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("username") String username) {
        checkUserHasPermission(username);

        UserDto user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Adds a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a newly created user for given username.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))})
    })
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
