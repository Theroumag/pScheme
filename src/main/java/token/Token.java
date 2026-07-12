package token;

import static token.Token.Type.*;
import java.util.regex.Pattern;
import java.util.Map;
import static java.util.Map.entry;
public class Token {
  public enum Type {
    NONE,
    // ----- For unknown characters; a lexing error has occurred. -----
    UNKNOWN,
    // ----- Punctuation/operator token types. Obvious lexemes. -----
    L_PAREN,
    R_PAREN,
    DOT,
    TICK,
    BACK_TICK,
    COMMA,
    COMMA_AT,
    HASH_F,
    HASH_T,
    DEFINE,
    IF,
    LAMBDA,
    QUOTE,
    QUASIQUOTE,
    UNQUOTE,
    BEGIN,
    SET_BANG,
    SET_CAR_BANG,
    SET_CDR_BANG,
    DEFINE_MACRO,
    UNQUOTE_SPLICING,
    SYMBOL,
    INTEGER,
    STRING,
    ERROR,
    EOF
  }

  public static final String symbolEndingCharacters = "()[]{},'`\"\\";

  /**
   * type -> lexeme map for Tokens that have only one lexeme possible.
   * Note: SYMBOL, INTEGER, STRING, ERROR are excluded.
   */
  public final static Map<Type, String> standardLexemes
    = Map.ofEntries(
                    entry(NONE, ""),
                    entry(L_PAREN, "("),
                    entry(R_PAREN, ")"),
                    entry(DOT, "."),
                    entry(TICK, "'"),
                    entry(BACK_TICK, "`"),
                    entry(COMMA, ","),
                    entry(COMMA_AT, ".@"),
                    entry(HASH_T, "#t"),
                    entry(HASH_F, "#f"),
                    entry(DEFINE, "define"),
                    entry(IF, "if"),
                    entry(LAMBDA, "lambda"),
                    entry(QUOTE, "quote"),
                    entry(QUASIQUOTE, "quasiquote"),
                    entry(UNQUOTE, "unquote"),
                    entry(BEGIN, "begin"),
                    entry(SET_BANG, "set!"),
                    entry(SET_CAR_BANG, "set-car!"),
                    entry(SET_CDR_BANG, "set-cdr!"),
                    entry(DEFINE_MACRO, "definemacro"),
                    entry(UNQUOTE_SPLICING, "unquote-splicing"),
                    entry(EOF, "")
           );

  /**
   * lexeme -> type map for "punctuation" type symbols.
   * Note: hash literals are handled separately
   */
  public final static Map<String, Type> typeForPunctuation
    = Map.ofEntries(
                    entry("(", L_PAREN),
                    entry(")", R_PAREN),
                    entry(".", DOT),
                    entry("'", TICK),
                    entry("`", BACK_TICK),
                    entry(",", COMMA),
                    entry(",@", COMMA_AT)
      );

  /**
   * lexeme -> type map for keyword symbols. Used to differentiate
   * between symbols and keywords.
   */
  public final static Map<String, Type> typeForSymbol
    = Map.ofEntries(
                    entry("(", L_PAREN),
                    entry(")", R_PAREN),
                    entry(".", DOT),
                    entry("'", TICK),
                    entry("`", BACK_TICK),
                    entry(",", COMMA),
                    entry(",@", COMMA_AT),
                    entry("define", DEFINE),
                    entry("if", IF),
                    entry("lambda", LAMBDA),
                    entry("quote", QUOTE),
                    entry("quasiquote", QUASIQUOTE),
                    entry("unquote", UNQUOTE),
                    entry("begin", BEGIN),
                    entry("set!", SET_BANG),
                    entry("set-car!", SET_CAR_BANG),
                    entry("set-cdr!", SET_CDR_BANG),
                    entry("definemacro", DEFINE_MACRO),
                    entry("unquote-splicing", UNQUOTE_SPLICING)
      );

  public static boolean isSymbolOrKeyword(Type ttype) {
    return ttype.equals(SYMBOL) || ((DEFINE.compareTo(ttype) <= 0) && (ttype.compareTo(UNQUOTE_SPLICING) <= 0)) ;
  }

  /**
   * Token factory. Give a lexeme, see if it matches any of the
   * singleton tokens. If it does, return a reference to the
   * singleton. If not, construct a new Symbol token for the lexeme.
   *
   * @param lexeme string to use to recognize Token or name of Symbol
   * token.
   * @return reference to singleton Token if lexeme matches; new
   * Symbol Token otherwise.
   */
  public static Token getToken(String fname, int line, int offset, String lexeme) {
    Type type = SYMBOL;
    if (typeForPunctuation.containsKey(lexeme))
      type = typeForPunctuation.get(lexeme);
    else if (isValidInteger(lexeme))
      type = INTEGER;
    return getToken(fname, line, offset, type, lexeme);
  }

  /**
   * Token factory. Give a type, construct a new Token with an empty
   * lexeme. Provided for symmetry with lexeme-only factory. May never
   * be called.
   *
   * @param type the type of the newly created Token.
   * @return new Token of given type with empty lexeme field.
   */
  public static Token getToken(String fname, int line, int offset, Type type) {
    return getToken(fname, line, offset, type, "");
  }

  /**
   * Token factory. Takes lexeme and type; using type, checks for
   * singleton Token and returns a match if found. Otherwise create a
   * new Token with given fields.
   *
   * @param type the type of the returned Token; used to index singletonTokens
   * @param lexeme lexeme field of returned Token IF not a singleton.
   * @return reference to singleton Token if type matches; new
   * Token of type otherwise.
   */
  public static Token getToken(String fname, int line, int offset, Type type, String lexeme) {
    return new Token(fname, line, offset, type, lexeme);
  }
  /**
   * Is the lexeme a valid integer for SchemING?
   * Valid integer = [+|-]d+
   * Optional +/- sign, at least one digit; anchored regex, no
   * whitespace.
   * @param lexeme string to check
   * @return true if the pattern is matched, false otherwise.
   */
  public static boolean isValidInteger(String lexeme) {
    return Pattern.matches("^[+-]?\\d+$", lexeme);
  }

  /**
   * Given a keyword, integer, or symbol: get the type.
   * Punctuation is left out because reading punctuation is different.
   */
  public static Type symbolType(String lexeme) {
    Type type = NONE;
    if (typeForSymbol.containsKey(lexeme))
      type = typeForSymbol.get(lexeme);
    else if (isValidInteger(lexeme))
      type = INTEGER;
    else
      type = SYMBOL;
    return type;
  }

  // --------------------------------------------------------------
  // Software Engineering Note: Token is _immutable_: it cannot be
  // changed after construction. This makes it possible to forego
  // private fields and getter-methods and just use
  // public FINAL fields.
  // --------------------------------------------------------------
  // The file location where the first character of the lexeme sits
  public final String fname;
  public final int line;
  public final int offset;

  // The type of the token
  public final Type type;
  // The actual character sequence read for this token
  public final String lexeme;

  /**
   * The constructor for an immutable token with the given values.
   *
   * @param line   line number where lexeme begins in source file
   * @param offset column number where lexeme begins in source file
   * @param type   token type
   * @param lexeme string read from the source file
   */
  public Token(String fname, int line, int offset, Type type, String lexeme) {
    this.fname = fname;
    this.line = line;
    this.offset = offset;
    this.type = type;
    this.lexeme = lexeme;
  }

  /**
   * Does this token match the given token type?
   * @return true if matching types; false otherwise
   */
  public boolean matches(Type type) {
    return this.type == type;
  }

  /**
   * Equality between tokens...
   *
   * Two tokens are equal iff they have the same type and the same
   * lexeme.
   * @param other object to check for equality
   * @note It would be possible to check file and position, too. Don't
   * feel like it.
   */
  public boolean equals(Object other) {
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof Token)) return false;
    Token oToken = (Token) other;
    boolean sameType = type.equals(oToken.type);
    boolean lexemeMatch = !meaningfulLexeme() || lexeme.equals(oToken.lexeme);
    return sameType && lexemeMatch;
  }

  /**
   * Does this Token have a non-standard lexeme?
   * @return true if lexeme should be shown with string representation;
   * false otherwise
   */
  private boolean meaningfulLexeme() {
    return (type == UNKNOWN) ||
      (type == SYMBOL) ||
      (type == INTEGER) ||
      (type == STRING) ||
      (type == ERROR);
  }

  /**
   * Provide string representation of the Token
   *
   * String rep is one of the two
   * <type>:<lexeme> [<fname> <line>:<offset>]
   * <type> [<fname> <line>:<offset>]
   *
   * @return string rep of this Token
   */
  @Override
  public String toString() {
    String lex = "";
    if (meaningfulLexeme())
      lex = String.format(": %s", lexeme);

    return String.format("%s%s [%s %d:%d]",
                         type.toString(), lex,
                         fname, line, offset);
  }

}
