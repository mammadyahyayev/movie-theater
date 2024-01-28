package az.aistgroup.util;

import az.aistgroup.domain.dto.UserDto;
import az.aistgroup.security.AuthorityConstant;
import az.aistgroup.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.Set;

/**
 * The class is used to populate the database with demo data.
 */
@Profile("!test")
public final class DataInitializer implements CommandLineRunner {
    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
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
