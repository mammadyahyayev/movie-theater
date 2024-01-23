package az.aistgroup.controller;

import az.aistgroup.domain.dto.LoginDto;
import az.aistgroup.domain.dto.UserDto;
import az.aistgroup.security.jwt.TokenGenerator;
import az.aistgroup.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthenticationController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    private final UserService userService;
    private final AuthenticationManagerBuilder authManagerBuilder;
    private final TokenGenerator tokenGenerator;

    public AuthenticationController(
            UserService userService,
            AuthenticationManagerBuilder authManagerBuilder,
            TokenGenerator tokenGenerator
    ) {
        this.userService = userService;
        this.authManagerBuilder = authManagerBuilder;
        this.tokenGenerator = tokenGenerator;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserDto userDto) {
        UserDto user = userService.addUser(userDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@Valid @RequestBody LoginDto loginDTO) {
        LOG.debug("Request to authenticate user {}", loginDTO.username());

        userService.checkLoginCredentials(loginDTO);

        var authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.username(),
                loginDTO.password()
        );

        Authentication authentication = authManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenGenerator.generateToken(authentication, loginDTO.isRememberMe());
        String refreshToken = tokenGenerator.generateToken(authentication, loginDTO.isRememberMe());
        return new ResponseEntity<>(new JwtToken(accessToken, refreshToken), HttpStatus.OK);
    }

    public record JwtToken(String accessToken, String refreshToken) {
    }
}
