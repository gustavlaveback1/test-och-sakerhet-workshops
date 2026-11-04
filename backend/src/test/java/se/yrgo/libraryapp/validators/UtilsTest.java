package se.yrgo.libraryapp.validators;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {

    @ParameterizedTest
    @CsvSource({
            "'', ''",
            "'bosse', 'bosse'",
            "'BOSSE', 'bosse'",
            "'BoSsE', 'bosse'",
            "'B!#@osse', 'bosse'",
            "'bosse😊', 'bosse'",
            "'B0S53', 'bs'",
            "'b o ss e', 'b o ss e'",
            "'bosseåäö', 'bosseåäö'",
            "'60553', ''",
            "'bosseボッセ', 'bosseボッセ'",
            "'bosse\nbredsladd', 'bosse\nbredsladd'",
    })

    void correctFiltering(String input, String expected) {
        String result = Utils.onlyLettersAndWhitespace(input);
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "'60553', 'bosse'",
            "'1337bosse', 'leetbosse'",
            "'1337b o s s e', 'leetb o s s e'",
            "'1337bössäå', 'leetbössäå'",
            "'6o89R055','bobross'"
    })

    void cleanAndUnLeet(String input, String expected) {
        assertThat(Utils.cleanAndUnLeet(input))
                .isEqualTo(expected);
    }
}
