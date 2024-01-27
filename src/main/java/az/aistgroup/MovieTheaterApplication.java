package az.aistgroup;

import az.aistgroup.security.AppSecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppSecurityProperties.class})
public class MovieTheaterApplication {
    public static void main(String[] args) {
        SpringApplication.run(MovieTheaterApplication.class, args);
    }
}
