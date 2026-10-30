package se.yrgo.libraryapp.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class UsernameTest {
    @ParameterizedTest
    @ValueSource(strings = {"bosse", "bosse1337", "B@ss.e1_1-2."})
    void correctUsername(String username) {
        assertThat(Username.validate(username)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"name with space", "bos", "bosse,!", "Bossääåö", "bosse😊", "ボッセ"})
    @EmptySource
    void incorrectUsername(String username) {
        assertThat(Username.validate(username)).isFalse();
    }
}
