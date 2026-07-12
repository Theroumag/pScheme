package env;

import ast.SExp;
import ast.Symbol;
import ast.exception.SchemeUndefinedSymbolException;

import java.util.HashMap;
import java.util.Map;

/**
 * An environment for evaluation of a Scheme {@link SExp}.
 *
 * Essentially a Map<Symbol, SExp> that supports chaining Env with
 * dynamic links to other Env.
 */
public class Env {
  // local symbol definitions
  private Map<Symbol, SExp> env;
  // pointer at parent environment
  private Env dynamicLink;

  /**
   * Default constructor. Empty environment with no parent.
   */
  public Env() {
    this(null, new HashMap<>());
  }

  /**
   * Primary constructor: set parent environment and map contents.
   * @param dynamicLink reference to parent Env
   * @param env Map of symbols/definitions to copy into this Env
   * @note env is shallow copied with a call to putAll
   */
  public Env(Env dynamicLink, Map<Symbol, SExp> env) {
    this.dynamicLink = dynamicLink;
    this.env = new HashMap<>();
    this.env.putAll(env);
  }

  /**
   * Copy environment contents with no parent Env.
   * @param env Map of symbols/definitions to copy into this Env
   * @note env is shallow copied with a call to putAll
   */
  public Env(Map<Symbol, SExp> env) {
    this(null, env);
  }

  /**
   * Construct empty environment below given parent.
   * @param dynamicLink reference to parent Env
   */
  public Env(Env dynamicLink) {
    this(dynamicLink, new HashMap<>());
  }

  /**
   * Look up the given symbol in the chain of Env starting here.
   * @param symbol the Symbol to lookup in the environment
   * @return the SExp with which symbol was defined
   * @throws SchemeUndefinedSymbolException if symbol is not found and
   * there is no parent Env in the chain
   */
  public SExp lookup(Symbol symbol) throws SchemeUndefinedSymbolException {
    if (env.containsKey(symbol))
      return env.get(symbol);
    if (dynamicLink != null)
      return dynamicLink.lookup(symbol);
    throw new SchemeUndefinedSymbolException("Lookup of \"%s\" failed.", symbol.symbol());
  }

  /**
   * Define (or redefine) symbol in the current environment.
   * @param symbol symbol being defined
   * @param sexp definition to be entered for symbol
   * @return symbol after it is defined
   */
  public SExp define(Symbol symbol, SExp sexp) {
    env.put(symbol, sexp);
    return symbol;
  }

  /**
   * Standard equals override.
   * @param other Object checked for equality
   * @return true if other is identical to this OR
   *   other is an Env with an equal map and equal parent
   */
  @Override
  public boolean equals(Object other) {
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof Env)) return false;

    Env oEnv = (Env)other;

    return env.equals(oEnv.env) &&
      ((dynamicLink == oEnv.dynamicLink) ||
       ((dynamicLink != null) && (dynamicLink.equals(oEnv.dynamicLink))));
  }
}
