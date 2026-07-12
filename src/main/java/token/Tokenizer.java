package token;

import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.Queue;
import java.util.LinkedList;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.IOException;

import io.SansCommentFilterReader;
import util.Trace;
import static util.Trace.*;
import static token.Token.Type.*;
import static token.Token.getToken;

public class Tokenizer {
  final private String fname;
  private Reader reader;
  private boolean done;
  private int putBack;

  private int lineOffset;
  private int lineNumber;
  private Token current;

  public Tokenizer(String filename, int startLineNumber, Reader source) {
    fname = filename;
    reader = new SansCommentFilterReader(source);
    setLineNumber(startLineNumber-1);
    newLine();
    done = false;
    putBack = -1;
  }

  public String getFileName() {
    return fname;
  }

  public int getLineOffset() {
    return lineOffset;
  }

  private void setLineOffset(int lineOffset) {
    this.lineOffset = lineOffset;
  }

  private void incLineOffset() {
    this.lineOffset += 1;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  private void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  private void incLineNumber() {
    this.lineNumber += 1;
  }

  static private boolean startsSchemeIdentifier(char ch) {
    return continuesSchemeIdentifier(ch) && ch != '#';
  }

  static private boolean continuesSchemeIdentifier(char ch) {
    return !Character.isWhitespace(ch) && ("()[],'`\"".indexOf(ch) < 0);
  }

  static private boolean startsSchemePunctuation(char ch) {
    return "().'`,".indexOf(ch) >= 0;
  }

  /**
   * Get the next available character.
   *
   * @return -1 on EOF, character read otherwise
   * @throws IOException
   */
  private int read() throws IOException {
    int ch;
    if (putBack == -1) {
      ch = reader.read();
    } else {
      ch = putBack;
      putBack = -1;
    }
    if ((char) ch == '\n') {
      newLine();
    } else {
      incLineOffset();
    }

    Trace.printf(TOKEN, String.format("< Tokenizer.read() [%d:%d] -> '%c'\n",
                                     getLineNumber(), getLineOffset(), (char)ch));

    return ch;
  }

  private void unread(int ch) {
    putBack = ch;
    lineOffset--;
  }

  /**
   * Get the next available, non-whitespace character.
   *
   * @return -1 on EOF, character read otherwise
   * @throws IOException
   */
  private int readOverWhitespace() throws IOException {
    int ch = read();
    while ((ch >= 0) && Character.isWhitespace(ch))
      ch = read();
    return ch;
  }

  private void newLine() {
    incLineNumber();
    setLineOffset(0);
    Trace.printf(TOKEN, String.format("Tokenizer: \\n %d:%d\n", getLineNumber(), getLineOffset()));
  }

  public boolean hasNext() {
    return !done;
  }

  public Token next() {
    Trace.printf(TOKEN, String.format("> Tokenizer.next()\n"));
    StringBuilder lexeme = new StringBuilder();
    if (done)
      return current;

    int lineNo = getLineNumber();
    int lineOff = getLineOffset();

    try {
      int ch = readOverWhitespace();
      lineNo = getLineNumber();
      lineOff = getLineOffset();
      Trace.printf(TOKEN, String.format("- Tokenizer.next() [%d:%d]\n", getLineNumber(), getLineOffset()));

      if (ch < 0) {
        done = true;
        current = getToken(getFileName(), lineNo, lineOff,Token.Type.EOF);
      } else {
      switch (ch) {
        case '(':
          current = getToken(getFileName(), lineNo, lineOff,Token.Type.L_PAREN);
          break;
        case ')':
          current = getToken(getFileName(), lineNo, lineOff,Token.Type.R_PAREN);
          break;
        case '.':
          current = getToken(getFileName(), lineNo, lineOff,Token.Type.DOT);
          break;
        case '\'':
          current = getToken(getFileName(), lineNo, lineOff,Token.Type.TICK);
          break;
        case '`':
          current = getToken(getFileName(), lineNo, lineOff,Token.Type.BACK_TICK);
          break;
        case ',':
          lexeme.append((char) ch);
          ch = read();
          if (ch < 0) {
            done = true;
            current = getToken(getFileName(), lineNo, lineOff,Token.Type.EOF);
            //return current;
          } else if (ch == '@') {
            current = getToken(getFileName(), lineNo, lineOff,Token.Type.COMMA_AT);
          } else {
            putBack = ch;
            current = getToken(getFileName(), lineNo, lineOff,Token.Type.COMMA);
          }
          break;
        case '#':
          lexeme.append((char) ch);
          ch = read();
          if (ch < 0) {
            done = true;
            current = getToken(getFileName(), lineNo, lineOff,Token.Type.EOF);
            // return current;
          } else {
          lexeme.append((char) ch);
          switch (ch) {
            case 'f':
              current = getToken(getFileName(), lineNo, lineOff,Token.Type.HASH_F);
              break;
            case 't':
              current = getToken(getFileName(), lineNo, lineOff,Token.Type.HASH_T);
              break;
            default:
              current = getToken(getFileName(), lineNo, lineOff,Token.Type.ERROR, String.format("Unknown # literal \"%s\"", lexeme.toString()));
              done = true;
              break;
          }
          }
          break;
        case '"':
          lexeme.append((char) ch);
          for (ch = read(); ch >= 0 && ch != '\n' && ch != '"'; ch = read()) {
            if (ch == '\\') {
              ch = read();
              switch (ch) {
                case 'n':
                  lexeme.append('\n');
                  break;
                case '"':
                case '\\':
                  lexeme.append((char) ch);
                  break;
                default:
                  current = getToken(getFileName(), lineNo, lineOff,Token.Type.ERROR, String.format("Unknown escape sequence \"\\%c\"", ch));
                  done = true;
                  //return current;
              }
            } else {
              lexeme.append((char) ch);
            }
          }
          if (ch == '"') {
            lexeme.append((char) ch);
            current = getToken(getFileName(), lineNo, lineOff,Token.Type.STRING, lexeme.toString());
          } else {
            current = getToken(getFileName(), lineNo, lineOff,Token.Type.ERROR, String.format("String terminated by end of line %s", lexeme.toString()));
            done = true;
          }

          break;
        default:
          while ((ch >= 0) && (Token.symbolEndingCharacters.indexOf((char) ch) < 0)
              && !Character.isWhitespace((char) ch)) {
            lexeme.append((char) ch);
            ch = read();
          }

          if (!Character.isWhitespace((char) ch))
            unread(ch);

          String str = lexeme.toString();

          // getToken can differentiate between INTEGER and SYMBOL
          current = getToken(getFileName(), lineNo, lineOff, str);
          break;
      }
      }
    } catch (IOException e) {
      current = getToken(getFileName(), lineNo, lineOff,Token.Type.ERROR, e.toString());
      done = true;
    }
    Trace.printf(TOKEN, String.format("< Tokenizer.next() -> %s\n", current));

    return current;
  }

  public Token curr() {
    return current;
  }
}
