package az.aistgroup.controller;

import az.aistgroup.domain.dto.LoginDto;
import az.aistgroup.domain.dto.RegisterDto;
import az.aistgroup.domain.dto.UserDto;
import az.aistgroup.domain.dto.UserView;
import az.aistgroup.exception.ErrorResponse;
import az.aistgroup.exception.TokenValidityException;
import az.aistgroup.security.TokenType;
import az.aistgroup.security.jwt.TokenProvider;
import az.aistgroup.service.UserService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

import java.util.Date;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthenticationController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    private final UserService userService;
    private final AuthenticationManagerBuilder authManagerBuilder;
    private final TokenProvider tokenProvider;

    public AuthenticationController(
            UserService userService,
            AuthenticationManagerBuilder authManagerBuilder,
            TokenProvider tokenProvider
    ) {
        this.userService = userService;
        this.authManagerBuilder = authManagerBuilder;
        this.tokenProvider = tokenProvider;
    }

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns newly registered user.",
                    content = {@Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "400", description = "Returns when validation for fields are failed.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @SecurityRequirements(value = {})
    @PostMapping("/register")
    public ResponseEntity<UserView> register(@Valid @RequestBody RegisterDto registerDto) {
        UserView user = userService.registerUser(registerDto);
        user.add(linkTo(AuthenticationController.class).slash("/login").withRel("login"));
        user.add(linkTo(UserController.class).slash("/balance/top-up").withRel("topUpBalance"));
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @Operation(summary = "Login a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns access token and refresh token for user.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtToken.class))}),
            @ApiResponse(responseCode = "400", description = "Returns when validation for fields are failed.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @SecurityRequirements(value = {})
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

        Map.Entry<String, Date> accessTokenInfo = tokenProvider.generateAccessToken(authentication);
        String accessToken = accessTokenInfo.getKey();
        Date accessTokenValidity = accessTokenInfo.getValue();

        Map.Entry<String, Date> refreshTokenInfo = tokenProvider.generateRefreshToken(authentication);
        String refreshToken = refreshTokenInfo.getKey();
        Date refreshTokenValidity = refreshTokenInfo.getValue();

        var token = new JwtToken(accessToken, accessTokenValidity, refreshToken, refreshTokenValidity);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @Operation(summary = "Get new access token by sending refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns access token and refresh token for user.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtToken.class))}),

            @ApiResponse(responseCode = "403", description = "Returns when refresh token is expired or given token is not a refresh token",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "400", description = "Returns when refresh token is null or empty",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/token/refresh")
    public ResponseEntity<JwtToken> getToken(@Valid @RequestBody RefreshTokenDto refreshTokenDto) {
        var refreshToken = refreshTokenDto.refreshToken();

        var tokenValidityResponse = tokenProvider.checkTokenValidity(refreshToken);
        if (!tokenValidityResponse.isValid()) {
            throw new TokenValidityException(TokenType.REFRESH_TOKEN, tokenValidityResponse.code());
        }

        Claims claims = tokenProvider.getClaims(refreshToken);
        if (!claims.get(TokenProvider.TOKEN_TYPE).equals(TokenType.REFRESH_TOKEN.toString())) {
            throw new TokenValidityException(TokenType.REFRESH_TOKEN, "Provided token is not a refresh token!");
        }

        Date refreshTokenValidity = claims.getExpiration();
        Authentication authentication = tokenProvider.getAuthentication(claims);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Map.Entry<String, Date> accessTokenInfo = tokenProvider.generateAccessToken(authentication);
        String accessToken = accessTokenInfo.getKey();
        Date accessTokenValidity = accessTokenInfo.getValue();

        var token = new JwtToken(accessToken, accessTokenValidity, refreshToken, refreshTokenValidity);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    public record RefreshTokenDto(
            @NotBlank(message = "Refresh token can not be null or empty!")
            String refreshToken
    ) {
    }

    public record JwtToken(String accessToken, Date accessTokenExpiresIn,
                           String refreshToken, Date refreshTokenExpiresIn) {

        @Override
        public String toString() {
            return "JWTToken";
        }
    }
}
