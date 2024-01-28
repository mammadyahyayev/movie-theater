package az.aistgroup.config;

import az.aistgroup.security.Auditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration class for enabling JPA Auditing.
 */
@Configuration
@EnableJpaAuditing
public class AuditConfiguration {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new Auditor();
    }
}
