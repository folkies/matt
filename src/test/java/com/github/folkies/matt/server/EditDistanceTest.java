package com.github.folkies.matt.server;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class EditDistanceTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
        tal, Montagmorgen, 1
        Dienstag, Donnerstag, 3
        Hamburg, Kreisstadt Marburg in Hessen, 2
    """)
    public void editDistance(String pattern, String text, int distance) {
        int[][] d = new int[2][100];
        assertThat(EditDistance.minEditDistance(pattern, text, d)).isEqualTo(distance);
    }
}
