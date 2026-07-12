package parser;

import java.io.Reader;


import ast.Atom;
import ast.Boolean;
import ast.Cons;
import ast.EOFExpression;
import ast.Error;
import ast.Number;
import ast.SExp;
import ast.Symbol;

import ast.exception.SchemeException;
import ast.exception.SchemeSyntaxException;
import ast.exception.SchemeTypeException;
import io.SansCommentFilterReader;
import token.Token;
import token.Tokenizer;
import token.Token.Type;
import util.Trace;

import static token.Token.Type.*;
import static util.Trace.*;

public class Parser {
  Tokenizer tokenSource;

  Token currToken;
  Token readAhead;
  SExp current;

  public Parser(String filename, int startLineNumber, Reader source) {
    tokenSource = new Tokenizer(filename, startLineNumber, source);
    currToken = null;
    current = null;
    readAhead = null;
  }

  public String getFileName() {
    return tokenSource.getFileName();
  }

  public int getLineNumber() {
    return tokenSource.getLineNumber();
  }

  public int getLineOffset() {
    return tokenSource.getLineOffset();
  }

  public boolean hasNext() {
    boolean more = (tokenSource.hasNext() &&
                    !(current instanceof EOFExpression));

    Trace.printf(PARSE, "%shasNext [%s]\n", (more?" ":"!"), currToken);
    return more;
  }

  public SExp curr() {
    return current;
  }

  public SExp next() throws SchemeSyntaxException, SchemeTypeException {
    Trace.printf(PARSE, String.format("> Parser.next\n"));
    current = read();
    Trace.printf(PARSE, String.format("< Parser.next: current = %s\n", current));

    return current;
  }

  private boolean match(Token.Type ttype) {
    return currToken.matches(ttype);
  }

  private Token readNextToken() {
    if (readAhead == null) {
      currToken = tokenSource.next();
    } else {
      currToken = readAhead;
      readAhead = null;
    }
    return currToken;
  }

  private void unreadToken(Token t) {
    Trace.printf(PARSE, String.format("= Parser.unreadToken(%s)\n", currToken));
    readAhead = t;
  }

  private void warn(String msg) {
    System.err.printf("<WARNING>: [%s %d:%d] %s\n", getFileName(), getLineNumber(), getLineOffset(), msg);
  }



  // recursive-descent parser
  private SExp read() throws SchemeSyntaxException, SchemeTypeException {
    SExp _form = null;
    readNextToken();
    Trace.printf(PARSE, "> Parser.read: currToken = %s\n", currToken);

    if (match(EOF)) {
      _form = EOFExpression.eofExpression();
    } else if (match(L_PAREN)) {
      _form = readTail(false);
      readNextToken();
      if (!match(R_PAREN)) {
        warn("Missing ')': ignored.");
        _form = read();
      }

    } else if (match(R_PAREN)) {
      warn("Unexpected ')'; ignored.");
      throw new SchemeSyntaxException("Unexpected \")\"");
      //_form = read();
    } else if (match(DOT)) {
      warn("Unexpected '.'; ignored.");
      throw new SchemeSyntaxException("Unexpected \".\"");
      // _form = read();
    } else if (match(TICK)) {
      _form = Cons.list(new Symbol("quote"), read());
    } else if (match(BACK_TICK)) {
      _form = Cons.list(new Symbol("quasiquote"), read());
    } else if (match(COMMA)) {
      _form = Cons.list(new Symbol("unquote"), read());
    } else if (match(COMMA_AT)) {
      _form = Cons.list(new Symbol("quasiquote-splicing"), read());
    } else if (match(HASH_F) || match(HASH_T)) {
      _form =  match(Type.HASH_T)?Boolean.True:Boolean.False;
    } else if (match(INTEGER)) {
      _form = new Number(Integer.parseInt(currToken.lexeme));
    } else if (match(SYMBOL)) {
      _form = new Symbol(currToken.lexeme);
    }
    Trace.printf(PARSE, "< Parser.read: _form = %s\n", _form);
    return _form;
  }

  static private int readTailDepth  = 0;
  private SExp readTail(boolean dotValid) throws SchemeSyntaxException, SchemeTypeException {
    SExp _form = null;
    readNextToken();
    Trace.printf(PARSE, "> Parser.readTail{%d}: currToken = %s\n", readTailDepth++, currToken);

    if (match(EOF)) {
      _form = EOFExpression.eofExpression();
      throw (new SchemeSyntaxException("Unexpeced EOF while parsing list"));
    } else if (match(R_PAREN)) {
      unreadToken(currToken);
      _form = Cons.cons();
    } else if (match(DOT)) {
      _form = read();
      readNextToken();
      if (!match(R_PAREN))
        throw new SchemeSyntaxException(String.format("Missing ')' after '.' (%s)", currToken));
      unreadToken(currToken);
    } else {
      Trace.printf(PARSE, "< Parser.readTail{%d}: Cons\n", readTailDepth-1, _form);
      unreadToken(currToken);
      _form = Cons.cons(read(), readTail(true));
    }
    Trace.printf(PARSE,"< Parser.readTail{%d}: _form = %s\n", --readTailDepth, _form);
    return _form;
  }

}
