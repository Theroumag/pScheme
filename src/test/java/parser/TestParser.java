package parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static token.Token.Type.*;

import ast.Atom;
import ast.Boolean;
import ast.Cons;
import ast.EOFExpression;
import ast.Number;
import ast.SExp;
import ast.Symbol;
import ast.exception.SchemeException;

import util.Trace;

class TestParser {
  /**
   * Does the Parser have more? Checked here to simplify changes in
   * Parser (if necessary).
   * @param parser Parser to check for additional SExp
   * @return true if next() should return something new; false
   * otherwise
   */
  boolean hasNext(Parser parser) throws SchemeException {
    return ! parser.next().equals(EOFExpression.eofExpression());
  }

  @Test
  public void testParserConstructor() throws SchemeException {
    StringReader inputReader = new StringReader("");
    Parser parser = new Parser("input-file", 1, inputReader);

    // empty parser should not be null; should not have next
    assertNotNull(parser);
    assertFalse(hasNext(parser));
  }


  @Test
  public void testHasNext_OneSymbol() throws SchemeException {
    String expString = "albatross";
    StringReader inputReader = new StringReader(expString);
    Parser parser = new Parser("input-file", 1, inputReader);

    assertNotNull(parser);
    assertTrue(hasNext(parser));

    SExp expected = new Symbol(expString);
    SExp actual = parser.curr();

    assertEquals(expected, actual);
    parser.next(); // note that next reads BEHIND to make REPL work
    assertFalse(parser.hasNext());
  }

  @Test
  public void testHasNext_OneInteger() throws SchemeException {
    String expString = "555";
    StringReader inputReader = new StringReader(expString);
    Parser parser = new Parser("input-file", 1, inputReader);

    assertNotNull(parser);
    assertTrue(parser.hasNext());

    SExp expected = new Number(Integer.parseInt(expString));
    SExp actual = parser.next();

    assertEquals(expected, actual);
    parser.next(); // note that next reads BEHIND to make REPL work
    assertFalse(parser.hasNext());
  }

  @Test
  public void testHasNext_Literals() throws SchemeException {
    String expString = """
      888 pelican robin
      #f #t
      8 1x 1000
      """;
      SExp[] expecteds = {
        new Number(888),
        new Symbol("pelican"),
        new Symbol("robin"),
        Boolean.False,
        Boolean.True,
        new Number(8),
        new Symbol("1x"), // not an Integer
        new Number(1000),
        EOFExpression.eofExpression()
    };

    StringReader inputReader = new StringReader(expString);
    Parser parser = new Parser("input-file", 1, inputReader);

    int i = 0;
    while (parser.hasNext()) {
      SExp actual = parser.next();
      SExp expected = expecteds[i];
      i++;

      assertEquals(expected, actual);
    }
    assertFalse(parser.hasNext());
  }

  @Test
  public void testHasNext_SomeLists() throws SchemeException {
    String expString = """
      ()
      (z)
      (a b c)
      """;
      SExp[] expecteds = {
        Cons.cons(),
        Cons.cons(new Symbol("z")),
        Cons.list(new Symbol("a"), new Symbol("b"), new Symbol("c")),
        EOFExpression.eofExpression()
    };

    StringReader inputReader = new StringReader(expString);
    Parser parser = new Parser("input-file", 1, inputReader);

    int i = 0;
    while (parser.hasNext()) {
      SExp actual = parser.next();
      SExp expected = expecteds[i];

      assertEquals(expected, actual);
      i++;
    }
    assertFalse(parser.hasNext());
  }

  @Test
  public void testHasNext_NestedLists() throws SchemeException {
    String expString = """
      (+ (* a b 3) (- 8 q))
      ( (z))
      (a (b (c)))
      """;
      SExp[] expecteds = {
        Cons.list(Symbol.symbol("+"),
                  Cons.list(Symbol.symbol("*"), Symbol.symbol("a"), Symbol.symbol("b"), Number.number(3)),
                  Cons.list(Symbol.symbol("-"), Number.number(8), Symbol.symbol("q"))),
        Cons.cons(Cons.cons(new Symbol("z"))),
        Cons.list(new Symbol("a"), Cons.cons(new Symbol("b"), Cons.cons(Cons.cons(new Symbol("c"))))),
        EOFExpression.eofExpression()
    };

    StringReader inputReader = new StringReader(expString);
    Parser parser = new Parser("input-file", 1, inputReader);

    int i = 0;
    while (parser.hasNext()) {
      SExp actual = parser.next();
      SExp expected = expecteds[i];
      assertEquals(expected, actual, String.format("Expression [%d]", i)
                  );
      i++;
    }
    assertFalse(parser.hasNext());
  }

  @Test
  public void testParsingWithDot() throws SchemeException {
    String expString = """
      (a . b)
      (q r s . 0)
      (#t . #f)
      ((w x . y) (e . f) . g)
      """;
    SExp[] expecteds = {
      Cons.cons(new Symbol("a"), new Symbol("b")),
      Cons.append(Cons.list(Symbol.symbol("q"), Symbol.symbol("r"), Symbol.symbol("s")), Number.number(0)),
      Cons.append(Cons.cons(Boolean.True), Boolean.False),
      Cons.append(Cons.list(Cons.append(Cons.list(Symbol.symbol("w"), Symbol.symbol("x")), Symbol.symbol("y")),
                            Cons.append(Cons.cons(Symbol.symbol("e")), Symbol.symbol("f"))), Symbol.symbol("g")),
      EOFExpression.eofExpression()
    };

    String[] expectedToStrings = {
      "(a . b)",
      "(q r s . 0)",
      "(#t . #f)",
      "((w x . y) (e . f) . g)",
      "#EOF"
    };

    StringReader inputReader = new StringReader(expString);
    Parser parser = new Parser("input-file", 1, inputReader);

    int i = 0;
    while (parser.hasNext()) {
      SExp actual = parser.next();
      SExp expected = expecteds[i];
      String expectedToString = expectedToStrings[i];
      String actualToString = actual.toString();

      assertEquals(expected, actual, String.format("Statement[%d]", i));
      assertEquals(expectedToString, actualToString);
      i++;
    }
    assertFalse(parser.hasNext());
  }

}
