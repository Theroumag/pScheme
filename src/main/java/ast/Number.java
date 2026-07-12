package ast;

import ast.exception.SchemeException;
import ast.exception.SchemeTypeException;
import env.Env;

import java.util.Objects;

public class Number extends Atom {
  final public int numericValue;

  public static Number number(int numericValue) {
    return new Number(numericValue);
  }

  public Number(int numericValue) {
    super();
    this.numericValue = numericValue;
  }

  public int value() {
    return this.numericValue;
  }

  public Number toNumber() throws SchemeTypeException {
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Number number1 = (Number) o;
    return numericValue == number1.numericValue;
  }

  @Override
  public int hashCode() {
    return Objects.hash(numericValue);
  }

  @Override
  public String toString() {
    return String.valueOf(value());
  }

  @Override
  public String toStringTail() {
    return ". " + toString();
  }

  // self-evaluating
  @Override
  public SExp eval(Env env) throws SchemeException {
    return this;
  }
}
