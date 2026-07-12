package io;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.io.StringReader;

class TestSansCommentFilterReader {
  @Test
  public void testEmpty() throws IOException {
    String input = "";
    StringReader str = new StringReader(input);
    SansCommentFilterReader testing = new SansCommentFilterReader(str);

    int ch;
    String output = "";
    while ((ch = testing.read()) >= 0)
      output += (char) ch;

    assertEquals(input, output);
  }

  @Test
  public void testNoComments() throws IOException {
    String input = """
      (define (square x)
        (* x x)
      )
      """;
    StringReader str = new StringReader(input);
    SansCommentFilterReader testing = new SansCommentFilterReader(str);

    int ch;
    String output = "";
    while ((ch = testing.read()) >= 0)
      output += (char) ch;

    assertEquals(input, output);
  }

  @Test
  public void testEOLNComments() throws IOException {
    String input = """
      ; blank?
      (define (puppy x) (caar x)); nothing worth while
      ;;
      """;
    StringReader str = new StringReader(input);
    SansCommentFilterReader testing = new SansCommentFilterReader(str);

    int ch;
    String output = "";
    while ((ch = testing.read()) >= 0)
      output += (char) ch;

    String expected = """

      (define (puppy x) (caar x))

      """;
    assertEquals(expected, output);
  }

  @Test
  public void testDelimitedOneLineComments() throws IOException {
    String input = """
      one #||# two
      (define (puppy x)#| |#(caar x)); nothing worth while

      """;
    StringReader str = new StringReader(input);
    SansCommentFilterReader testing = new SansCommentFilterReader(str);

    int ch;
    String output = "";
    while ((ch = testing.read()) >= 0)
      output += (char) ch;

    String expected = """
      one   two
      (define (puppy x) (caar x))

      """;
    assertEquals(expected, output);
  }

  @Test
  public void testDelimitedComments() throws IOException {
    String input = """
      one#|
      |# two
      (define (puppy x)#| |#(caar x))#|
      ; nothing worth while
      |# one-more
      (apply something)
      """;
    StringReader str = new StringReader(input);
    SansCommentFilterReader testing = new SansCommentFilterReader(str);

    int ch;
    String output = "";
    while ((ch = testing.read()) >= 0)
      output += (char) ch;

    String expected = """
      one
       two
      (define (puppy x) (caar x))

       one-more
      (apply something)
      """;
    assertEquals(expected, output);
  }

}
