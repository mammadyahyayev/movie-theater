package az.aistgroup.security;

import az.aistgroup.exception.AccessDeniedExceptionHandler;
import az.aistgroup.exception.UnAuthorizedExceptionHandler;
import az.aistgroup.security.jwt.JwtFilter;
import az.aistgroup.security.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Configuration
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final AppSecurityProperties securityProperties;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public SecurityConfig(
            TokenProvider tokenProvider,
            AppSecurityProperties securityProperties, HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.tokenProvider = tokenProvider;
        this.securityProperties = securityProperties;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var jwtFilter = new JwtFilter(tokenProvider, securityProperties);

        http
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new UnAuthorizedExceptionHandler(handlerExceptionResolver))
                        .accessDeniedHandler(new AccessDeniedExceptionHandler(handlerExceptionResolver))
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/token/refresh").permitAll()
                        .requestMatchers("/api/**").authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/api-docs/**", "/swagger-ui/**");
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
