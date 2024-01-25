package az.aistgroup;

import az.aistgroup.domain.dto.UserDto;
import az.aistgroup.repository.UserRepository;
import az.aistgroup.security.AppSecurityProperties;
import az.aistgroup.security.AuthorityConstant;
import az.aistgroup.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.math.BigDecimal;
import java.util.Set;

@SpringBootApplication
@EnableConfigurationProperties({AppSecurityProperties.class})
public class MovieTheaterApplication implements CommandLineRunner {
    private final UserService userService;
    private final UserRepository userRepository;

    public MovieTheaterApplication(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(MovieTheaterApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String username = "mammadyahyayev";

        boolean isExist = userService.doesUserExist(username);
        if (isExist) return;

        var adminUser = new UserDto();
        adminUser.setUsername(username);
        adminUser.setFirstName("Mammad");
        adminUser.setLastName("Yahyayev");
        adminUser.setFatherName("Rufat");
        adminUser.setBalance(new BigDecimal("1000"));
        adminUser.setPassword("12345678");
        adminUser.setAuthorities(Set.of(AuthorityConstant.USER, AuthorityConstant.ADMIN));
        userService.addUser(adminUser);
    }
}
