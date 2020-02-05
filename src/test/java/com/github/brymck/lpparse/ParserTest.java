package com.github.brymck.lpparse;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    // List<String> metadataItems = Arrays.asList("\\ENCODING=ISO-8859-1", "\\Problem name:
    // ilog.cplex");
    // List<String> boundsItems = Collections.emptyList();
    Problem expected =
        Problem.builder()
            .withObjective(new Statement("obj", Arrays.asList("-", "x", "-", "y")))
            .withSubjectTo(
                Arrays.asList(new Statement("blah", Arrays.asList("x", "-", "y", "=", "0"))))
            .withBounds(
                Arrays.asList(
                    new Bounds(Arrays.asList("0", "<=", "x", "<=", "1")),
                    new Bounds(Arrays.asList("0.2", "<=", "y", "<=", "1"))))
            .withGenerals(Arrays.asList(new Variable("x")))
            .withSemiContinuous(Arrays.asList(new Variable("y")))
            .build();
    assertThat(actual).isEqualTo(expected);
  }
}
