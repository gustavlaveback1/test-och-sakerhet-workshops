package se.yrgo.libraryapp.services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import se.yrgo.libraryapp.dao.UserDao;
import se.yrgo.libraryapp.entities.*;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class UserServiceTest {
    @Mock
    private UserDao userDao;

    @Test
    @SuppressWarnings("deprecation")
    void correctLogin() {
        final String userId = "1";
        final UserId id = UserId.of(userId);
        final String username = "testuser";
        final String password = "password";
        final String passwordHash = "password";
        final LoginInfo info = new LoginInfo(id, passwordHash);
        final PasswordEncoder encoder = org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();

        when(userDao.getLoginInfo(username)).thenReturn(Optional.of(info));

        UserService userService = new UserService(userDao, encoder);

        assertThat(userService.validate(username,
                password)).isEqualTo(Optional.of(id));
    }

    @Test
    @SuppressWarnings("deprecation")
    void incorrectLogin() {
        final String username = "testuser";
        final String password = "password";
        final PasswordEncoder encoder = org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();

        when(userDao.getLoginInfo(username))
                .thenReturn(Optional.empty());

        UserService userService = new UserService(userDao, encoder);

        assertThat(userService.validate(username, password))
                .isEmpty();
    }

    @Test
    @SuppressWarnings("deprecation")
    void correctUserWrongPassword() {
        final String userId = "1";
        final UserId id = UserId.of(userId);
        final String username = "testuser";
        final String password = "wrongpassword";
        final String passwordHash = "password";
        final LoginInfo info = new LoginInfo(id, passwordHash);
        final PasswordEncoder encoder = org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();

        when(userDao.getLoginInfo(username)).thenReturn(Optional.of(info));

        UserService userService = new UserService(userDao, encoder);

        assertThat(userService.validate(username,
                password)).isEmpty();
    }

    @Test
    void registerUser() {
        final String username = "testuser";
        final String realname = "Test User";
        final String password = "password";
        final String passwordHash = "hashedPassword";

        PasswordEncoder encoder = mock(PasswordEncoder.class);

        when(encoder.encode(password)).thenReturn(passwordHash);

        when(userDao.register(username, realname, passwordHash))
                .thenReturn(true);

        UserService userService = new UserService(userDao, encoder);

        assertThat(userService.register(username, realname, password)).isTrue();
    }

    @Test
    void registerUserWithApostrophe() {
        final String username = "testuser";
        final String realname = "Ian O'Toole";
        final String password = "password";
        final String passwordHash = "hashedPassword";

        PasswordEncoder encoder = mock(PasswordEncoder.class);

        when(encoder.encode(password)).thenReturn(passwordHash);

        when(userDao.register(username, "Ian O\\'Toole", passwordHash))
                .thenReturn(true);

        UserService userService = new UserService(userDao, encoder);

        assertThat(userService.register(username, realname, password)).isTrue();
    }

    @Test
    void usernameIsAvailable() {
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        when(userDao.isNameAvailable("testuser")).thenReturn(true);

        UserService userService = new UserService(userDao, encoder);

        assertThat(userService.isNameAvailable("testuser")).isTrue();
    }

    @Test
    void usernameIsNotAvailable() {
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        when(userDao.isNameAvailable("testuser")).thenReturn(false);

        UserService userService = new UserService(userDao, encoder);

        assertThat(userService.isNameAvailable("testuser")).isFalse();
    }

    @Test
    void usernameTooShort() {
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        UserService userService = new UserService(userDao, encoder);

        assertThat(userService.isNameAvailable("bo")).isFalse();
    }
}