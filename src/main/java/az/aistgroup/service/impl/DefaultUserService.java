package az.aistgroup.service.impl;

import az.aistgroup.domain.dto.*;
import az.aistgroup.domain.entity.User;
import az.aistgroup.exception.ResourceAlreadyExistException;
import az.aistgroup.exception.ResourceNotFoundException;
import az.aistgroup.repository.AuthorityRepository;
import az.aistgroup.repository.UserRepository;
import az.aistgroup.security.AuthorityConstant;
import az.aistgroup.service.UserService;
import az.aistgroup.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static az.aistgroup.domain.entity.User.DEFAULT_USER_BALANCE;
import static az.aistgroup.domain.entity.User.DEFAULT_USER_ROLE;

@Service
public class DefaultUserService implements UserService, UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;

    public DefaultUserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthorityRepository authorityRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("username can not be null or empty!");
        }

        return userRepository.findUserByUsername(username)
                .map(this::createSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " was not found!"));
    }

    private UserDetails createSecurityUser(final User user) {
        var authorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities
        );
    }


    @Override
    @Transactional(readOnly = true)
    public List<UserViewDto> getAllUsers() {
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
                .orElseThrow(() -> new ResourceNotFoundException("User " + username + " was not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean doesUserExist(String username) {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("username can not be null or empty!");
        }

        return userRepository.findUserByUsername(username).isPresent();
    }

    @Override
    @Transactional
    public UserView registerUser(final RegisterDto registerDto) {
        Objects.requireNonNull(registerDto, "registerDto can not be null!");

        userRepository.findUserByUsername(registerDto.getUsername())
                .ifPresent(u -> {
                    throw new ResourceAlreadyExistException(
                            "Username '%s' is already exist!".formatted(registerDto.getUsername())
                    );
                });

        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setFatherName(registerDto.getFatherName());
        user.setUsername(registerDto.getUsername().toLowerCase());
        user.setBalance(new BigDecimal(DEFAULT_USER_BALANCE));

        String encodedPassword = passwordEncoder.encode(registerDto.getPassword());
        user.setPassword(encodedPassword);

        authorityRepository.findById(DEFAULT_USER_ROLE)
                .ifPresent(user::addAuthority);

        userRepository.save(user);
        LOG.debug("User registered {} on {}", user, LocalDateTime.now());

        return new UserView(user);
    }

    @Override
    @Transactional
    public UserDto addUser(final UserDto userDto) {
        Objects.requireNonNull(userDto, "userDto can not be null!");

        userRepository.findUserByUsername(userDto.getUsername())
                .ifPresent(u -> {
                    throw new ResourceAlreadyExistException(
                            "Username '%s' is already exist!".formatted(userDto.getUsername())
                    );
                });

        var user = new User();
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

        Set<String> authorities = userDto.getAuthorities();
        if (authorities.isEmpty()) {
            authorityRepository.findById(AuthorityConstant.USER)
                    .ifPresent(user::addAuthority);
        } else {
            authorities.forEach(authority ->
                    authorityRepository.findById(authority).ifPresent(user::addAuthority)
            );
        }

        userRepository.save(user);
        LOG.debug("User added {} on {}", user, LocalDateTime.now());

        return new UserDto(user);
    }

    @Override
    public UserDto updateUser(String username, UpdateUserRequestDto userDto) {
        Objects.requireNonNull(userDto, "userDto can not be null!");

        var user = userRepository.findUserByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User '" + username + "' not found!"));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setFatherName(userDto.getFatherName());

        userRepository.save(user);
        LOG.debug("User updated {} on {}", user, LocalDateTime.now());
        return new UserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUserByAdmin(String username, UserDto userDto) {
        Objects.requireNonNull(userDto, "userDto can not be null!");

        var user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User '" + username + "' not found!"));

        // user wants to change his username
        if (!user.getUsername().equals(userDto.getUsername())) {
            userRepository.findUserByUsername(userDto.getUsername())
                    .ifPresentOrElse((u) -> {
                        throw new ResourceAlreadyExistException("Username '" + u.getUsername() + "' is already exist!");
                    }, () -> {
                        user.setUsername(userDto.getUsername());
                    });
        }

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setFatherName(userDto.getFatherName());
        user.setBalance(userDto.getBalance());

        var authorities = userDto.getAuthorities();
        if (authorities != null && !authorities.isEmpty()) {
            authorities.forEach(
                    authority -> authorityRepository.findById(authority).ifPresent(user::addAuthority)
            );
        }

        this.userRepository.save(user);
        LOG.debug("User updated {} on {}", user, LocalDateTime.now());
        return new UserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(final String username) {
        Objects.requireNonNull(username, "username cannot be null!");

        var user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with " + username + " not found!"));

        this.userRepository.delete(user);
        LOG.debug("User {} deleted.", username);
    }

    @Override
    public UserDto topUpBalance(TopUpBalanceDto topUpBalanceDto) {
        Objects.requireNonNull(topUpBalanceDto, "topUpBalanceDto cannot be null!");

        String username = topUpBalanceDto.getUsername();
        var user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User '" + username + "' not found!"));

        user.increaseBalance(topUpBalanceDto.getAmount());

        userRepository.save(user);
        return new UserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public void checkLoginCredentials(LoginDto loginDto) {
        LOG.debug("validating credentials for {}", loginDto.username());
        var user = this.userRepository.findUserByUsername(loginDto.username())
                .orElseThrow(
                        () -> new BadCredentialsException("User with '%s' not found!".formatted(loginDto.username()))
                );

        if (!this.passwordEncoder.matches(loginDto.password(), user.getPassword())) {
            throw new BadCredentialsException("Username or password is invalid!");
        }
    }
}
