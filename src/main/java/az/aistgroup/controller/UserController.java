package az.aistgroup.controller;

import az.aistgroup.domain.dto.*;
import az.aistgroup.exception.ErrorResponse;
import az.aistgroup.security.AuthorityConstant;
import az.aistgroup.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static az.aistgroup.security.SecurityUtils.checkUserHasPermission;

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
                            schema = @Schema(implementation = UserView.class))})
    })
    @GetMapping
    @PreAuthorize("hasAuthority(\"" + AuthorityConstant.ADMIN + "\")")
    public ResponseEntity<List<UserView>> getAllUsers() {
        List<UserView> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Fetch a user by his username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return a user for given username.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),

            @ApiResponse(responseCode = "404", description = "Returns given username isn't found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "403",
                    description = "Given username and logged in user's username aren't matched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

    })
    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable("username") String username) {
        checkUserHasPermission(username);

        UserDto user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Adds a new user. Only ADMIN can create a new user. Users must register to create account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a newly created user for given username.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),

            @ApiResponse(responseCode = "403",
                    description = "Given username and logged in user's username aren't matched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),

            @ApiResponse(responseCode = "400", description = "Returns when validation for fields are failed.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping
    @PreAuthorize("hasAuthority(\"" + AuthorityConstant.ADMIN + "\")")
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto userDto) {
        UserDto user = userService.addUser(userDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @Operation(summary = "Updates a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a newly updated user.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),

            @ApiResponse(responseCode = "404", description = "Returns given username isn't found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "403",
                    description = "Returns when given username and logged in user's username aren't matched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),

            @ApiResponse(responseCode = "400", description = "Returns when validation for fields are failed.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PutMapping("/{username}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable("username") String username,
            @Valid @RequestBody UpdateUserRequestDto userDto
    ) {
        checkUserHasPermission(username);

        UserDto user = userService.updateUser(username, userDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Deletes a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a successful operation result.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OperationResponseDto.class))}),

            @ApiResponse(responseCode = "404", description = "Returns given username isn't found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "403",
                    description = "Returns when given username and logged in user's username aren't matched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<OperationResponseDto> deleteUser(@PathVariable("username") String username) {
        checkUserHasPermission(username);

        userService.deleteUser(username);
        var response = new OperationResponseDto(true, "User '" + username + "' deleted...");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Increase user's balance.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a successful operation result.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OperationResponseDto.class))}),

            @ApiResponse(responseCode = "404", description = "Returns given username isn't found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "403",
                    description = "Returns when given username and logged in user's username aren't matched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),

            @ApiResponse(responseCode = "400",
                    description = "When given amount is negative or format of amount is not correct. Amount format must be: e.g 34.75",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/balance/top-up")
    public ResponseEntity<OperationResponseDto> increaseBalance(@Valid @RequestBody TopUpBalanceDto topUpBalanceDto) {
        String username = topUpBalanceDto.getUsername();
        checkUserHasPermission(username);

        UserDto userDto = userService.topUpBalance(topUpBalanceDto);
        String responseMessage = "User '" + username + "' balance increased to " + userDto.getBalance();
        var response = new OperationResponseDto(true, responseMessage);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
