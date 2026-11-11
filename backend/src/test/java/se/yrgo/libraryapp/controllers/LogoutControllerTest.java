package se.yrgo.libraryapp.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.yrgo.libraryapp.dao.SessionDao;
import io.jooby.Context;
import io.jooby.Cookie;

@ExtendWith(MockitoExtension.class)
public class LogoutControllerTest {

    @Mock
    SessionDao sessionDao;

    @Mock
    Context context;

    LogoutController logoutController;

    @BeforeEach
    void setUp() {
        logoutController = new LogoutController(
                sessionDao);
    }

    @Test
    void logoutWithValidSession() {
        UUID sessionId = UUID.randomUUID();

        logoutController.logout(context, sessionId.toString());

        verify(sessionDao).delete(sessionId);
        verify(context).setResponseCookie(any(Cookie.class));
    }

    @Test
    void logoutWithNullCookie() {
        logoutController.logout(context, null);

        verify(sessionDao, never()).delete(any());
        verify(context, never()).setResponseCookie(any(Cookie.class));
    }

    @Test
    void logoutWithInvalidSessionCookie() {
        logoutController.logout(context, "invalid-uuid");

        verify(sessionDao, never()).delete(any());
        verify(context).setResponseCookie(any(Cookie.class));
    }
}
