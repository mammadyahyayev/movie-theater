package az.aistgroup.controller;

import az.aistgroup.exception.ErrorResponseCode;
import az.aistgroup.security.TokenType;
import az.aistgroup.security.jwt.JwtTokenProvider;
import az.aistgroup.security.jwt.TokenProvider;
import az.aistgroup.util.TestUtils;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TokenProvider tokenProvider;

    @Test
    void returnTokenExpiredResponse_whenGivenRefreshTokenExpired() throws Exception {
        var response = new JwtTokenProvider.TokenValidityResponse(false, ErrorResponseCode.TOKEN_EXPIRED);
        when(tokenProvider.checkTokenValidity(Mockito.anyString())).thenReturn(response);

        var refreshTokenDto = new AuthenticationController.RefreshTokenDto("refresh_token");

        mockMvc.perform(post("/api/v1/auth/token/refresh")
                        .content(TestUtils.toJson(refreshTokenDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors.[0].code").value(ErrorResponseCode.REFRESH_TOKEN_EXPIRED.getCode()))
                .andExpect(jsonPath("$.errors.[0].field", nullValue()));

    }

    @DisplayName("""
            Returns FORBIDDEN response when getting a new access token with refresh token because given refresh\s
            token is not refresh token.""")
    @Test
    void returnForbiddenResponse_whenGivenTokenIsNotRefreshToken() throws Exception {
        String accessToken = "access_token";

        when(tokenProvider.checkTokenValidity(accessToken))
                .thenReturn(new JwtTokenProvider.TokenValidityResponse(true, null));

        var claims = new DefaultClaims(Map.of(TokenProvider.TOKEN_TYPE, TokenType.ACCESS_TOKEN));
        when(tokenProvider.getClaims(accessToken)).thenReturn(claims);

        var refreshTokenDto = new AuthenticationController.RefreshTokenDto(accessToken);

        mockMvc.perform(post("/api/v1/auth/token/refresh")
                        .content(TestUtils.toJson(refreshTokenDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors.[0].code").value(ErrorResponseCode.ACCESS_DENIED.getCode()))
                .andExpect(jsonPath("$.errors.[0].message").value("Provided token is not a refresh token!"))
                .andExpect(jsonPath("$.errors.[0].field", nullValue()));
    }
}