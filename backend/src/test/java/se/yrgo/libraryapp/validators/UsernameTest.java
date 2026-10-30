package se.yrgo.libraryapp.validators;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsernameTest {
    @Test
    void correctUsername() {
        assertThat(Username.validate("bosse")).isTrue();
    }

    @Test
    void incorrectUsername() {
        assertThat(Username.validate("name with space")).isFalse();
    }

    @Test
    void usernameWithNumber() {
        assertThat(Username.validate("bosse1337")).isTrue();
    }

    @Test
    void correctUsernameWithSpecialChars() {
        assertThat(Username.validate("B@s,s.e,1_1-2.")).isTrue();
    }

    @Test
    void incorrectUsernameWithSpecialCharacters() {
        assertThat(Username.validate("bosse!")).isFalse();
    }

    @Test
    void usernameUnderFourCharactersLong() {
        assertThat(Username.validate("bos")).isFalse();
    }

    @Test
    void usernameWithSwedishCharacters() {
        assertThat(Username.validate("Bossääåö")).isFalse();
    }

    @Test
    void emptyUsername(){
        assertThat(Username.validate("")).isFalse();
    }

}
