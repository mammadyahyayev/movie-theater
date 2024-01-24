package az.aistgroup.service.impl;

import az.aistgroup.domain.dto.LoginDto;
import az.aistgroup.domain.dto.UserDto;
import az.aistgroup.domain.entity.User;
import az.aistgroup.exception.ResourceAlreadyExistException;
import az.aistgroup.exception.ResourceNotFoundException;
import az.aistgroup.repository.UserRepository;
import az.aistgroup.service.UserService;
import az.aistgroup.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("username can not be null or empty!");
        }

        return userRepository.findUserByUsername(username)
                .map(UserDto::new)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User " + username + " was not found!")
                );
    }

    @Override
    @Transactional
    public UserDto addUser(final UserDto userDto) {
        Objects.requireNonNull(userDto, "userDto can not be null!");

        userRepository.findUserByUsername(userDto.getUsername())
                .ifPresent(u -> {
                    throw new ResourceAlreadyExistException(
                            "Username %s is already exist!".formatted(userDto.getUsername())
                    );
                });

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setFatherName(userDto.getFatherName());
        user.setUsername(userDto.getUsername().toLowerCase());

        if (userDto.getBalance() == null) {
            user.setBalance(new BigDecimal("100"));
        } else {
            user.setBalance(userDto.getBalance());
        }

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(encodedPassword);

        this.userRepository.save(user);
        LOG.debug("User added {} on {}", user, LocalDateTime.now());

        return new UserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        Objects.requireNonNull(userDto, "userDto can not be null!");

        User user = userRepository.findUserByUsername(userDto.getUsername())
                .orElseThrow(() -> new ResourceAlreadyExistException(
                        "Username %s is already exist!".formatted(userDto.getUsername())
                ));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setFatherName(userDto.getFatherName());
        user.setBalance(userDto.getBalance());

        this.userRepository.save(user);
        LOG.debug("User updated {} on {}", user, LocalDateTime.now());
        return new UserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(final String username) {
        Objects.requireNonNull(username, "username cannot be null!");

        userRepository.findUserByUsername(username)
                .map(user -> {
                    this.userRepository.delete(user);
                    LOG.debug("User {} deleted.", username);
                    return user;
                })
                .orElseThrow(() -> new ResourceNotFoundException("User with " + username + " not found!"));
    }

    @Override
    public void checkLoginCredentials(LoginDto loginDto) {
        LOG.debug("validating credentials for {}", loginDto.username());
        this.userRepository.findUserByUsername(loginDto.username())
                .map(user -> {
                    if (!this.passwordEncoder.matches(loginDto.password(), user.getPassword())) {
                        throw new BadCredentialsException("Username or password is invalid!");
                    }
                    return user;
                }).orElseThrow(
                        () -> new BadCredentialsException("User with '%s' not found!".formatted(loginDto.username()))
                );
    }
}
