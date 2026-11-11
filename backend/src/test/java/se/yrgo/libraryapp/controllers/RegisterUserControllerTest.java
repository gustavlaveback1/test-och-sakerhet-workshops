package se.yrgo.libraryapp.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.yrgo.libraryapp.dao.UserDao;
import se.yrgo.libraryapp.entities.forms.RegisterUserData;

@ExtendWith(MockitoExtension.class)
public class RegisterUserControllerTest {

    @Mock 
    UserDao userDao;
    
    RegisterUserController registerUserController;

    @BeforeEach
    void setUp() {
        registerUserController = new RegisterUserController(userDao);
    }

    @Test
    void registerUserWithValidData() {
        RegisterUserData registerUserData = new RegisterUserData("testuser",
                "Test User",
                "password123");

        when(userDao.register("testuser",
                "Test User",
                "password123")).thenReturn(true);

        boolean result = registerUserController.register(registerUserData);

        assertThat(result).isTrue();
    }

    @Test
    void registerUserWithInvalidUsernameShouldNotRegister() {
        RegisterUserData registerUserData = new RegisterUserData("",
                "Test User",
                "password123");

        boolean result = registerUserController.register(registerUserData);

        assertThat(result).isFalse();
    }

    @Test
    void registerUserWithInvalidRealNameShouldNotRegister() {
        RegisterUserData registerUserData = new RegisterUserData("testuser",
                "",
                "password123");

        boolean result = registerUserController.register(registerUserData);

        assertThat(result).isFalse();
    }

    @Test
    void registerUserWhenDaoFailsShouldBeFalse() {
        RegisterUserData registerUserData = new RegisterUserData("testuser",
                "Test User",
                "password123");

        when(userDao.register("testuser",
                "Test User",
                "password123"))
                .thenReturn(false);

        boolean result = registerUserController.register(registerUserData);

        assertThat(result).isFalse();
    }

    @Test
    void nameIsAvailable() {
        when(userDao.isNameAvailable("testuser")).thenReturn(true);

        boolean result = registerUserController.isNameAvailable("testuser");

        assertThat(result).isTrue();
    }

    @Test
    void nameIsNotAvailable() {
        when(userDao.isNameAvailable("testuser")).thenReturn(false);

        boolean result = registerUserController.isNameAvailable("testuser");

        assertThat(result).isFalse();
    }
}
