package com.github.brymck.lpparse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserTest {
  private Parser parser = new Parser();
  private String fileName = "model.lp";
  private ClassLoader classLoader = ClassLoader.getSystemClassLoader();
  private File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
  private FileInputStream stream;

  @BeforeEach
  void init() throws FileNotFoundException {
    stream = new FileInputStream(file);
  }

  @Test
  void blah() throws IOException {
    Problem actual = parser.parse(stream);
    Problem expected =
      Problem.builder()
            .withObjective(
                new Objective(
                    Sense.MINIMIZE, new Statement("obj", asList("-", "x", "-", "y"))))
            .withSubjectTo(
                asList(new Statement("blah", asList("x", "-", "y", "=", "0"))))
            .withBounds(
                asList(
                    new Bounds(asList("0", "<=", "x", "<=", "1")),
                    new Bounds(asList("0.2", "<=", "y", "<=", "1"))))
            .withGenerals(asList(new Variable("x")))
            .withSemiContinuous(asList(new Variable("y")))
            .build();
    assertThat(actual).isEqualTo(expected);
  }
}
