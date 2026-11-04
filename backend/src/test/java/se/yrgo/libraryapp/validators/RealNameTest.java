package se.yrgo.libraryapp.validators;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class RealNameTest {

    @ParameterizedTest
    @ValueSource(strings = { "fan", "helvete", "idiot", "Bosse idiot", "idiotbosse", "LOKETFRÅNHELVETET" })
    @EmptySource
    void badWordsAreInvalid(String name) {
        assertThat(RealName.validate(name)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = { "Bosse", "Sten", "Loket", "Bengt" })
    void normalNamesAreValid(String name) {
        assertThat(RealName.validate(name)).isTrue();
    }

}
