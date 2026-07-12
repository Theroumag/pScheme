package token;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import static token.Token.Type.*;

class TestTokenizer {
  @Test
  public void testConstructor() {
    StringReader inputReader = new StringReader("");
    Tokenizer tokenizer = new Tokenizer("fileName", 1, inputReader);
    assertNotNull(tokenizer);
    assertTrue(tokenizer.hasNext());
    Token eof = tokenizer.next();
    assertFalse(tokenizer.hasNext());

    String input = """
      (+ 5 3)
      """;
    inputReader = new StringReader(input);
    tokenizer = new Tokenizer("fileName", 1, inputReader);
    assertNotNull(tokenizer);
    assertTrue(tokenizer.hasNext());
  }

  @Test
  public void testALittleOfEach() {
    String testFilename = "testFilename";
    String input = """
      (+ 5 3)
      """;
    StringReader inputReader = new StringReader(input);
    Tokenizer tokenizer = new Tokenizer(testFilename, 1, inputReader);

    Token[] expectedTokens
      = {
      new Token(testFilename, 1, 1,  L_PAREN, "("),
      new Token(testFilename, 1, 2,  SYMBOL, "+"),
      new Token(testFilename, 1, 4,  INTEGER, "5"),
      new Token(testFilename, 1, 6,  INTEGER, "3"),
      new Token(testFilename, 1, 7,  R_PAREN, ")"),
      new Token(testFilename, 2, 1,  EOF, "")
    };

    int i = 0;
    while (tokenizer.hasNext()) {
      Token token = tokenizer.next();
      Token expected = expectedTokens[i++];

      assertEquals(expected, token);
      assertEquals(expected.fname, token.fname);
      assertEquals(expected.line, token.line, String.format("Line number incorrect %s", token.type));
      assertEquals(expected.offset, token.offset, String.format("Line offset incorrect %s", token.type));
    }
  }

  @Test
  public void testOneSymbol() {
    String testFilename = "testFilename";
    String input = "one";
    StringReader inputReader = new StringReader(input);
    Tokenizer tokenizer = new Tokenizer(testFilename, 1, inputReader);

    Token[] expectedTokens
      = {
      new Token(testFilename, 1, 1,  SYMBOL, "one"),
      new Token(testFilename, 1, 4,  EOF, "")
    };

    int i = 0;
    while (tokenizer.hasNext()) {
      Token token = tokenizer.next();
      Token expected = expectedTokens[i++];

      assertEquals(expected, token);
      assertEquals(expected.fname, token.fname);
      assertEquals(expected.line, token.line, String.format("Line number incorrect %s", token.type));
      assertEquals(expected.offset, token.offset, String.format("Line offset incorrect %s", token.type));
    }
  }

  @Test
  public void testOneSymbolWithEOLNSpace() {
    String testFilename = "testFilename";
    String input = "one ";
    StringReader inputReader = new StringReader(input);
    Tokenizer tokenizer = new Tokenizer(testFilename, 1, inputReader);

    Token[] expectedTokens
      = {
      new Token(testFilename, 1, 1,  SYMBOL, "one"),
      new Token(testFilename, 1, 5,  EOF, "")
    };

    int i = 0;
    while (tokenizer.hasNext()) {
      Token token = tokenizer.next();
      Token expected = expectedTokens[i++];

      assertEquals(expected, token);
      assertEquals(expected.fname, token.fname);
      assertEquals(expected.line, token.line, String.format("Line number incorrect %s", token.type));
      assertEquals(expected.offset, token.offset, String.format("Line offset incorrect %s", token.type));
    }
  }
  @Test
  public void testEveryTokenType() {
    String testFilename = "everyTokenType";
    String input = """
      ; vvvv line 2 vvvv
      (define (square n)
         (* n n)
      )
      (if (eq '(a) symbol))
      #|

      |#
      define
          begin
          lambda set! set-car! set-cdr!
          definemacro unquote-splicing
          quote begin quasiquote unquote

          ( ) . a.b 12ab -1- -100 23000
          #f #t one-two-three
          #| 17 |#
      """;
    StringReader inputReader = new StringReader(input);
    Tokenizer tokenizer = new Tokenizer(testFilename, 1, inputReader);

    /*
    Token[] expectedTokens
      = {
    new Token(testFilename, 2,  1, L_PAREN, "("),
    new Token(testFilename, 2,  2, DEFINE, "define"),
    new Token(testFilename, 2,  9, L_PAREN, "("),
    new Token(testFilename, 2,  10, SYMBOL, "square"),
    new Token(testFilename, 2,  17, SYMBOL, "n"),
    new Token(testFilename, 2,  18, R_PAREN, ")"),
    new Token(testFilename, 3,  4, L_PAREN, "("),
    new Token(testFilename, 3,  5, SYMBOL, "*"),
    new Token(testFilename, 3,  7, SYMBOL, "n"),
    new Token(testFilename, 3,  9, SYMBOL, "n"),
    new Token(testFilename, 3,  10, R_PAREN, ")"),
    new Token(testFilename, 4,  1, R_PAREN, ")"),
    new Token(testFilename, 5,  1, L_PAREN, "("),
    new Token(testFilename, 5,  2, IF, "if"),
    new Token(testFilename, 5,  5, L_PAREN, "("),
    new Token(testFilename, 5,  6, SYMBOL, "eq"),
    new Token(testFilename, 5,  9, TICK, "'"),
    new Token(testFilename, 5,  10, L_PAREN, "("),
    new Token(testFilename, 5,  11, SYMBOL, "a"),
    new Token(testFilename, 5,  12, R_PAREN, ")"),
    new Token(testFilename, 5,  14, SYMBOL, "symbol"),
    new Token(testFilename, 5,  20, R_PAREN, ")"),
    new Token(testFilename, 5,  21, R_PAREN, ")"),
    new Token(testFilename, 9,  1, DEFINE, "define"),
    new Token(testFilename, 10,  5, BEGIN, "begin"),
    new Token(testFilename, 11,  5, LAMBDA, "lambda"),
    new Token(testFilename, 11,  12, SET_BANG, "set!"),
    new Token(testFilename, 11,  17, SET_CAR_BANG, "set-car!"),
    new Token(testFilename, 11,  26, SET_CDR_BANG, "set-cdr!"),
    new Token(testFilename, 12,  5, DEFINE_MACRO, "definemacro"),
    new Token(testFilename, 12,  17, UNQUOTE_SPLICING, "unquote-splicing"),
    new Token(testFilename, 13,  5, QUOTE, "quote"),
    new Token(testFilename, 13,  11, BEGIN, "begin"),
    new Token(testFilename, 13,  17, QUASIQUOTE, "quasiquote"),
    new Token(testFilename, 13,  28, UNQUOTE, "unquote"),
    new Token(testFilename, 15,  5, L_PAREN, "("),
    new Token(testFilename, 15,  7, R_PAREN, ")"),
    new Token(testFilename, 15,  9, DOT, "."),
    new Token(testFilename, 15,  11, SYMBOL, "a.b"),
    new Token(testFilename, 15,  15, SYMBOL, "12ab"),
    new Token(testFilename, 15,  20, SYMBOL, "-1-"),
    new Token(testFilename, 15,  24, INTEGER, "-100"),
    new Token(testFilename, 15,  29, INTEGER, "23000"),
    new Token(testFilename, 16,  5, HASH_F, "#f"),
    new Token(testFilename, 16,  8, HASH_T, "#t"),
    new Token(testFilename, 16,  11, SYMBOL, "one-two-three"),
    new Token(testFilename, 17,  6, EOF, ""),
    };
    */
    Token[] expectedTokens
      = {
    new Token(testFilename, 2,  1, L_PAREN, "("),
    new Token(testFilename, 2,  2, SYMBOL, "define"),
    new Token(testFilename, 2,  9, L_PAREN, "("),
    new Token(testFilename, 2,  10, SYMBOL, "square"),
    new Token(testFilename, 2,  17, SYMBOL, "n"),
    new Token(testFilename, 2,  18, R_PAREN, ")"),
    new Token(testFilename, 3,  4, L_PAREN, "("),
    new Token(testFilename, 3,  5, SYMBOL, "*"),
    new Token(testFilename, 3,  7, SYMBOL, "n"),
    new Token(testFilename, 3,  9, SYMBOL, "n"),
    new Token(testFilename, 3,  10, R_PAREN, ")"),
    new Token(testFilename, 4,  1, R_PAREN, ")"),
    new Token(testFilename, 5,  1, L_PAREN, "("),
    new Token(testFilename, 5,  2, SYMBOL, "if"),
    new Token(testFilename, 5,  5, L_PAREN, "("),
    new Token(testFilename, 5,  6, SYMBOL, "eq"),
    new Token(testFilename, 5,  9, TICK, "'"),
    new Token(testFilename, 5,  10, L_PAREN, "("),
    new Token(testFilename, 5,  11, SYMBOL, "a"),
    new Token(testFilename, 5,  12, R_PAREN, ")"),
    new Token(testFilename, 5,  14, SYMBOL, "symbol"),
    new Token(testFilename, 5,  20, R_PAREN, ")"),
    new Token(testFilename, 5,  21, R_PAREN, ")"),
    new Token(testFilename, 9,  1, SYMBOL, "define"),
    new Token(testFilename, 10,  5, SYMBOL, "begin"),
    new Token(testFilename, 11,  5, SYMBOL, "lambda"),
    new Token(testFilename, 11,  12, SYMBOL, "set!"),
    new Token(testFilename, 11,  17, SYMBOL, "set-car!"),
    new Token(testFilename, 11,  26, SYMBOL, "set-cdr!"),
    new Token(testFilename, 12,  5, SYMBOL, "definemacro"),
    new Token(testFilename, 12,  17, SYMBOL, "unquote-splicing"),
    new Token(testFilename, 13,  5, SYMBOL, "quote"),
    new Token(testFilename, 13,  11, SYMBOL, "begin"),
    new Token(testFilename, 13,  17, SYMBOL, "quasiquote"),
    new Token(testFilename, 13,  28, SYMBOL, "unquote"),
    new Token(testFilename, 15,  5, L_PAREN, "("),
    new Token(testFilename, 15,  7, R_PAREN, ")"),
    new Token(testFilename, 15,  9, DOT, "."),
    new Token(testFilename, 15,  11, SYMBOL, "a.b"),
    new Token(testFilename, 15,  15, SYMBOL, "12ab"),
    new Token(testFilename, 15,  20, SYMBOL, "-1-"),
    new Token(testFilename, 15,  24, INTEGER, "-100"),
    new Token(testFilename, 15,  29, INTEGER, "23000"),
    new Token(testFilename, 16,  5, HASH_F, "#f"),
    new Token(testFilename, 16,  8, HASH_T, "#t"),
    new Token(testFilename, 16,  11, SYMBOL, "one-two-three"),
    new Token(testFilename, 18,  1, EOF, ""),
    };
    int i = 0;
    while (tokenizer.hasNext()) {
      Token token = tokenizer.next();
      Token expected = expectedTokens[i++];

      assertEquals(expected, token);
      assertEquals(expected.fname, token.fname);
      assertEquals(expected.line, token.line, String.format("Line number incorrect %s", token.type));
      assertEquals(expected.offset, token.offset, String.format("Line offset incorrect %s", token.type));
    }
  }
}
