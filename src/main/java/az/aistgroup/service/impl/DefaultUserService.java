package az.aistgroup.service.impl;

import az.aistgroup.domain.dto.LoginDto;
import az.aistgroup.domain.dto.UserDto;
import az.aistgroup.domain.entity.User;
import az.aistgroup.exception.ResourceAlreadyExistException;
import az.aistgroup.repository.UserRepository;
import az.aistgroup.service.UserService;
import az.aistgroup.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;

public class DefaultUserService implements UserService, UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("username can not be null or empty!");
        }

        return userRepository.findUserByUsername(username)
                .map(this::createSecurityUser)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User " + username + " was not found!")
                );
    }

    private UserDetails createSecurityUser(final User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), Collections.emptyList()
        );
    }


    @Override
    public UserDto addUser(final UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("userDto can not be null");
        }

        userRepository.findUserByUsername(userDto.getUsername())
                .ifPresent(u -> {
                    throw new ResourceAlreadyExistException(
                            "Username %s is already exist!".formatted(userDto.getUsername())
                    );
                });

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setFatherName(user.getFatherName());
        user.setUsername(userDto.getUsername().toLowerCase());

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(encodedPassword);

        this.userRepository.save(user);
        LOG.debug("User registered {} on {}", user, LocalDateTime.now());

        return new UserDto(user);
    }

    @Override
    public void checkLoginCredentials(LoginDto loginDto) {

    }
}
