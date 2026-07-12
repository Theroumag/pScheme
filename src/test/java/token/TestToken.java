package token;

import org.junit.jupiter.api.Test;

import token.Token.Type;

import static org.junit.jupiter.api.Assertions.*;

import static token.Token.Type.*;
import java.util.Map;
import static java.util.Map.entry;

class TestToken {
  @Test
  public void testConstructor() {
    String fileName = "input_file.scm";
    int line = 1;
    int offset = 1;
    Token.Type type = Token.Type.NONE;
    String lexeme = "";
    Token token = new Token(fileName, line, offset, type, lexeme);

    assertNotNull(token);
    assertEquals(fileName, token.fname);
    assertEquals(line, token.line);
    assertEquals(offset, token.offset);
    assertEquals(type, token.type);
    assertEquals(lexeme, token.lexeme);
  }

  @Test
  public void testEquals() {
    String fileName = "something.scm";
    int line = 1;
    int offset = 1;
    Map<Type, String> symbolIntString =
        Map.ofEntries(
                      entry(UNKNOWN, "Unknown character in input stream"),
                      entry(SYMBOL, "an-atom"),
                      entry(INTEGER, "101"),
                      entry(STRING, "\"Hello, Wordl!\""),
                      entry(ERROR, "Parse error")
                      );

    for (Type type : Type.values()) {
      String lexeme = null;
      if (symbolIntString.containsKey(type))
        lexeme = symbolIntString.get(type);
      else
        lexeme = Token.standardLexemes.get(type);

      Token a = new Token(fileName, line, offset, type, lexeme);
      offset += lexeme.length();
      if (offset >= 80) {
        offset = 1;
        line++;
      }
      Token b = new Token(fileName, line, offset, type, lexeme);
      offset += lexeme.length();
      if (offset >= 80) {
        offset = 1;
        line++;
      }

      assertEquals(a, b);
      assertEquals(b, a);
      assertEquals(a, a);
    }
  }


}
