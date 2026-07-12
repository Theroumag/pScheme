package ast;

import java.util.Map;
import static java.util.Map.entry;

import ast.exception.SchemeException;
import ast.exception.SchemeTypeException;
import env.Env;

import java.util.Objects;

public class Cons extends SExp {
  private static final Cons _null = new Cons(null, null);
  public static final Map<Symbol, String> specials = Map.ofEntries(
      entry(new Symbol("quote"), "'"),
      entry(new Symbol("quasiquote"), "`"),
      entry(new Symbol("unquote"), ","),
      entry(new Symbol("unquote-splicing"), ",@"));

  private SExp _car;
  private SExp _cdr;

  private Cons(SExp car, SExp cdr) {
    this._car = car;
    this._cdr = cdr;
  }

  public static Cons cons() {
    return _null;
  }

  public static Cons cons(SExp car) {
    return new Cons(car, _null);
  }

  public static Cons cons(SExp car, SExp cdr) {
    return new Cons(car, cdr);
  }

  public static Cons list(SExp... sExps) {
    Cons result = _null;
    int s = sExps.length;
    while (s > 0) {
      --s;
      result = new Cons(sExps[s], result);
    }
    return result;
  }

  public static SExp append(Cons list, SExp lastItem) throws SchemeTypeException {
    if (list.equals(_null))
      return lastItem;
    else
      return list.append(lastItem);
  }

  public Cons append(SExp lastItem) throws SchemeTypeException {
    if (cdr().equals(_null))
      _cdr = lastItem;
    else
      try {
        cdr().toCons().append(lastItem);
      } catch (SchemeException e) {
        e.printStackTrace();
      }
    return this;
  }

  public SExp car() {
    return _car;
  }

  public void car(SExp _car) {
    this._car = _car;
  }

  public SExp cdr() throws SchemeTypeException {
    if (this == _null)
      throw new SchemeTypeException(
          String.format("The object %s, passed as first argument to cdr is not the correct type", this));
    return _cdr;
  }

  public void cdr(SExp _cdr) {
    this._cdr = _cdr;
  }

  @Override
  public Cons toCons() throws SchemeTypeException {
    return this;
  }

  public SExp nth(int n) throws SchemeException {
    if (this == _null)
      throw new SchemeTypeException(
          String.format("The object %s, passed as first argument to 'nth' is not the correct type"));
    if (n == 0)
      return _car;
    return _cdr.toCons().nth(n - 1);
  }

  public int length() throws SchemeException {
    if (this == _null)
      return 0;
    else
      return 1 + _cdr.toCons().length();
  }

  @Override
  public boolean truthValue() {
    return !this.equals(_null);
  }

  @Override
  public String toString() {
    return "(" +
        toStringTail() +
        ")";
  }

  @Override
  public String toStringTail() {
    if (this == _null)
      return "";
    String tail = _cdr.toStringTail();
    return _car.toString() +
        ((tail != "") ? (" " + tail) : "");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Cons cons = (Cons) o;
    return Objects.equals(_car, cons._car) &&
        Objects.equals(_cdr, cons._cdr);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_car, _cdr);
  }

  @Override
  public SExp eval(Env env) throws SchemeException {
    if (this.equals(_null))
      return _null;
    
    // Evaluate the first element to get the function
    SExp function = _car.eval(env);
    
    // If it's not a callable, throw an error
    if (!(function instanceof Callable)) {
      throw new SchemeTypeException("First element of list must evaluate to a function");
    }
    
    // Apply the function to the rest of the list
    return function.apply(env, _cdr);
  }

  /**
   * Evaluates each element in the cons list recursively
   * @param env Environment to evaluate in
   * @return New cons list with evaluated elements
   * @throws SchemeException if evaluation fails
   * @author Karl Schreyer
   * @email schreyk206@potsdam.edu
   * @course CIS 443 Programming Languages
   * @assignment p05 pScheme
   * @due 05/09/2025
   */
  public Cons evalCons(Env env) throws SchemeException {
    if (this == _null)
      return this;
    return new Cons(_car.eval(env), _cdr.toCons().evalCons(env));
  }
  
}
