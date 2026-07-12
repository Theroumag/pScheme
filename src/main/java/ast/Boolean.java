package ast;

import ast.exception.SchemeException;
import env.Env;

import java.util.Objects;

public class Boolean extends SExp {
  public final static Boolean True = new Boolean(true);
  public final static Boolean False = new Boolean(false);
  private final boolean value;

  private Boolean(boolean value) {
    this.value = value;
  }

  @Override
  public Boolean toBoolean() {
    return this;
  }

  @Override
  public boolean truthValue() {
    return value;
  }

  @Override
  public String toString() {
    return value ? "#t" : "#f";
  }

  @Override
  public String toStringTail() {
    return ". " + toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Boolean aBoolean = (Boolean) o;
    return value == aBoolean.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  // self-evaluating
  @Override
  public SExp eval(Env env) throws SchemeException {
    return this;
  }
}
