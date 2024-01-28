package az.aistgroup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Configuration class for beans that used throughout project.
 */
@Configuration
public class BeanConfiguration {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
