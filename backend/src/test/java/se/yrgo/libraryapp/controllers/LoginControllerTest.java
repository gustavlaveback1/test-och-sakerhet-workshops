package se.yrgo.libraryapp.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.jooby.Context;
import io.jooby.Cookie;
import io.jooby.StatusCode;
import se.yrgo.libraryapp.dao.RoleDao;
import se.yrgo.libraryapp.dao.SessionDao;
import se.yrgo.libraryapp.entities.Role;
import se.yrgo.libraryapp.entities.UserId;
import se.yrgo.libraryapp.entities.forms.LoginData;
import se.yrgo.libraryapp.services.UserService;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

        @Mock
        UserService userService;

        @Mock
        RoleDao roleDao;

        @Mock
        SessionDao sessionDao;

        @Mock
        Context context;

        LoginController loginController;

        @BeforeEach
        void setUp() {
                loginController = new LoginController(
                                userService,
                                roleDao,
                                sessionDao);
        }

        @Test
        void successfulLogin() {
                UserId userId = UserId.of(1);
                UUID sessionId = UUID.randomUUID();

                LoginData loginData = new LoginData("testuser", "password");

                List<Role> expectedRoles = List.of(Role.USER);

                when(userService.validate("testuser", "password"))
                                .thenReturn(Optional.of(userId));

                when(sessionDao.create(userId))
                                .thenReturn(sessionId);

                when(roleDao.get(userId))
                                .thenReturn(expectedRoles);

                List<Role> result = loginController.login(context, null, loginData);

                assertThat(result).containsExactly(Role.USER);

                verify(context).setResponseCookie(any(Cookie.class));

                verify(userService).validate("testuser", "password");
        }

        @Test
        void unsuccessfulLogin() {

                LoginData loginData = new LoginData("testuser", "password");

                when(userService.validate("testuser", "password"))
                                .thenReturn(Optional.empty());

                List<Role> result = loginController.login(context, null, loginData);

                assertThat(result).isEmpty();

                verify(context).setResponseCode(StatusCode.UNAUTHORIZED);
        }

        @Test
        void loginWithValidSession() {
                UserId userId = UserId.of(1);
                UUID sessionId = UUID.randomUUID();

                LoginData loginData = new LoginData("testuser", "password");

                when(sessionDao.validate(sessionId))
                                .thenReturn(userId);

                List<Role> result = loginController.login(
                                context, sessionId.toString(), loginData);

                assertThat(result).isEmpty();
        }

        @Test
        void loginWithInvalidSession() {
                LoginData loginData = new LoginData("testuser", "password");

                when(userService.validate("testuser", "password"))
                                .thenReturn(Optional.empty());

                List<Role> result = loginController.login(
                                context, "invalid session", loginData);

                assertThat(result).isEmpty();

                verify(context).setResponseCode(StatusCode.UNAUTHORIZED);
        }

        @Test
        void isLoggedInWithValidSession() {
                UUID sessionId = UUID.randomUUID();
                UserId userId = UserId.of(1);

                List<Role> expectedRoles = List.of(Role.USER);

                when(sessionDao.validate(sessionId))
                                .thenReturn(userId);

                when(roleDao.get(userId))
                                .thenReturn(expectedRoles);

                List<Role> result = loginController.isLoggedIn(
                                sessionId.toString());

                assertThat(result).containsExactly(Role.USER);
        }

        @Test
        void isLoggedInWithInvalidSession() {
                List<Role> result = loginController.isLoggedIn("invalid-session");

                assertThat(result).isEmpty();
        }

        @Test
        void isLoggedInWithNullSession() {
                List<Role> result = loginController.isLoggedIn(null);

                assertThat(result).isEmpty();
        }

}