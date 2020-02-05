package com.github.brymck.lpparse;

import java.io.*;
import java.util.*;
import org.jetbrains.annotations.NotNull;

final class TokenIterator implements Iterator<Token> {
  private @NotNull BufferedReader reader;
  private @NotNull Queue<@NotNull Token> tokens;

  protected TokenIterator(@NotNull InputStream inputStream) {
    reader = new BufferedReader(new InputStreamReader(inputStream));
    tokens = new LinkedList<>();
  }

  @Override
  public boolean hasNext() {
    try {
      return !tokens.isEmpty() || reader.ready();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private @NotNull Token convertStringToToken(int position, @NotNull String s) {
    if (position == 0) {
      if (s.endsWith(":")) {
        return new Token(s.substring(0, s.length() - 1), TokenType.LABEL);
      } else {
        return new Token(s, TokenType.VARIABLE);
      }
    } else {
      return new Token(s, TokenType.VARIABLE);
    }
  }

  private @NotNull String stripComments(String line) {
    int firstBackslashIndex = line.indexOf("\\");
    if (firstBackslashIndex != -1) {
      return line.substring(0, firstBackslashIndex);
    } else {
      return line;
    }
  }

  private void readMoreTokens() {
    try {
      String line = reader.readLine();
      line = stripComments(line);
      Keyword keyword = Keyword.tryParseString(line);
      if (keyword == null) {
        StringTokenizer stringTokenizer = new StringTokenizer(line);
        int position = 0;
        while (stringTokenizer.hasMoreElements()) {
          String string = stringTokenizer.nextToken();
          Token token = convertStringToToken(position, string);
          tokens.add(token);
          position++;
        }
      } else {
        Token token = new Token(keyword.name(), TokenType.KEYWORD);
        tokens.add(token);
      }
      tokens.add(new Token("", TokenType.LINE_BREAK));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @NotNull Token next() {
    if (!hasNext()) {
      throw new RuntimeException("no more items");
    }
    if (tokens.isEmpty()) {
      readMoreTokens();
    }
    Token token = tokens.poll();
    if (token == null) {
      throw new RuntimeException("blah");
    }
    return token;
  }
}
