package az.aistgroup.controller;

import az.aistgroup.domain.dto.TopUpBalanceDto;
import az.aistgroup.domain.dto.UserDto;
import az.aistgroup.domain.entity.User;
import az.aistgroup.exception.ErrorResponseCode;
import az.aistgroup.repository.UserRepository;
import az.aistgroup.security.AuthorityConstant;
import az.aistgroup.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static az.aistgroup.util.TestUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    private static final String DEFAULT_USERNAME = "default";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @WithMockUser(authorities = AuthorityConstant.ADMIN)
    @Test
    void testGetAllUsers() throws Exception {
        var user = new UserDto();
        user.setFirstName("Mammad");
        user.setLastName("Yahyayev");
        user.setFatherName("Rufat");
        user.setUsername("mammadyahyaYEV");
        user.setPassword("12345678");
        user.setBalance(new BigDecimal("120"));

        userService.addUser(user);

        var secondUser = new UserDto();
        secondUser.setFirstName("Jack");
        secondUser.setLastName("Jack");
        secondUser.setFatherName("Jack");
        secondUser.setUsername("jacK");
        secondUser.setPassword("12345678");
        secondUser.setBalance(new BigDecimal("10"));

        userService.addUser(secondUser);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.[0].lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.[0].fatherName").value(user.getFatherName()))
                .andExpect(jsonPath("$.[0].username").value(user.getUsername().toLowerCase()))
                .andExpect(jsonPath("$.[0].password").doesNotExist())
                .andExpect(jsonPath("$.[0].balance", is(user.getBalance().doubleValue())));
    }

    @Test
    void throwAccessDeniedException_whenTokenNotFound() throws Exception {
        String username = "user-without-token";
        mockMvc.perform(get("/api/v1/users/" + username))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors.[0].code").value(ErrorResponseCode.UNAUTHORIZED.getCode()))
                .andExpect(jsonPath("$.errors.[0].field", nullValue()));
    }

    @WithMockUser(authorities = AuthorityConstant.USER, username = DEFAULT_USERNAME)
    @Test
    void testTopUpBalance() throws Exception {
        var savedUser = userRepository.save(getDefaultUser(DEFAULT_USERNAME));

        var topUpBalanceDto = new TopUpBalanceDto();
        topUpBalanceDto.setUsername(DEFAULT_USERNAME);
        topUpBalanceDto.setAmount(new BigDecimal(30));

        mockMvc.perform(
                        post("/api/v1/users/balance/top-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(topUpBalanceDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message", containsString(topUpBalanceDto.getUsername())))
                .andExpect(jsonPath("$.message", containsString(topUpBalanceDto.getAmount().toString())));

        Optional<User> userByUsername = userRepository.findUserByUsername(savedUser.getUsername());
        assertTrue(userByUsername.isPresent());
        var user = userByUsername.get();

        assertThat(user.getBalance()).isGreaterThan(savedUser.getBalance());

        var increasedAmount = savedUser.getBalance().add(topUpBalanceDto.getAmount()).doubleValue();
        assertThat(user.getBalance().doubleValue()).isEqualTo(increasedAmount);

        userRepository.deleteById(savedUser.getId());
    }

    private User getDefaultUser(String username) {
        var user = new User();
        user.setFirstName("Jack");
        user.setLastName("Doe");
        user.setFatherName("John");
        user.setUsername(username);
        user.setPassword("12345678");
        user.setBalance(new BigDecimal("200"));
        return user;
    }

}