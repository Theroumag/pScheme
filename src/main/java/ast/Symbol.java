package ast;

import ast.exception.SchemeException;
import ast.exception.SchemeTypeException;
import env.Env;

import java.util.Objects;

public class Symbol extends Atom {
    final public String symbol_name;

    public static Symbol symbol(String symbol_name) {
      return new Symbol(symbol_name);
    }

  public Symbol(String symbol_name) {
        super();
        this.symbol_name = symbol_name;
    }

    @Override
    public Symbol toSymbol() throws SchemeTypeException {
        return this;
    }

    @Override
    public String toString() {
        return symbol_name;
    }

    @Override
    public String toStringTail() {
      return ". " + toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol otherSymbol = (Symbol) o;
        return Objects.equals(symbol_name, otherSymbol.symbol_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol_name);
    }

  public String symbol() {
    return symbol_name;
  }

  // look me up
  @Override
  public SExp eval(Env env) throws SchemeException {
    return env.lookup(this);
  }
}
