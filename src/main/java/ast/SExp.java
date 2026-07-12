package ast;

import ast.exception.SchemeException;
import ast.exception.SchemeTypeException;
import env.Env;

public class SExp {
  public Cons toCons()
    throws SchemeTypeException {
    throw new SchemeTypeException(String.format("Attempt to cast non-Cons (%s)to Cons", this));
  }

  public Number toNumber()
    throws SchemeTypeException {
    throw new SchemeTypeException(String.format("Attempt to cast non-Number (%s) to Number", this));
  }

  public Symbol toSymbol()
    throws SchemeTypeException {
    throw new SchemeTypeException(String.format("Attempt to cast non-Symbol (%s) to Number", this));
  }

  public Boolean toBoolean()
    throws SchemeTypeException {
    throw new SchemeTypeException(String.format("Attempt to cast non-Boolean (%s) to Boolean", this));
  }

  public boolean truthValue() {
    return false;
  }


  public String toString() {
    return "";
  }

  protected String toStringTail() {
    return toString();
  }

  @Override
  public boolean equals(Object other) {
    if (other == null)
      return false;
    if (other == this)
      return true;
    return false;
  }

  public SExp eval(Env env) throws SchemeException {
    throw new SchemeTypeException("Evaluating a non-evaluatable object");
  }

  public SExp apply(Env env, SExp args) throws SchemeException {
    throw new SchemeTypeException("Applying a non-applicable object");
  }
}
