/**
 * Global is a singleton class that contains the global environment.
 * 
 * Global contains built-in functions and procedures of the Scheme language.
 * It is used to store the global environment for the Scheme interpreter.
 * Inside the Global class, the built-in functions of arithmetic operations, 
 * list operations, and comparison operations are defined.
 *
 * @author Karl Schreyer
 * @email schreyk206@potsdam.edu
 * @course CIS 443 Programming Languages
 * @assignment p05 pScheme
 * @due 05/09/2025
 */

package env;

import ast.*;
import ast.Boolean;
import ast.exception.SchemeTypeException;
import ast.Number;

import java.util.Map;
import static java.util.Map.entry;

/**
 * Global
 *
 */
public class Global
  extends Env {

  /**
   * PRIVATE Assign given map of defines into the Global environment
   * @param env Map of symbols/definitions to copy into this Env
   * @note env is shallow copied with a call to putAll
   */
  private Global(Map<Symbol, SExp> env) {
    super(null, env);
  }

  /**
   * Static function to get the singleton Global.env().
   * @return the one Global containing the global environment.
   */
  public static Global env() {
    return _global;
  }

  /**
   * The Global.env(); the one table with all of the methods declared.
   */
  private final static Global _global = new Global(
      Map.ofEntries(
          /**
           * "+" is a built-in function.
           * It is a standard function so all of it's parameters are
           * evaluated before it is actually called (evalAll).
           *
           * Function is to take evaluated value of first and second
           * parameters and add them, returning a new Number. This is
           * done with a single-line lambda function implementing the
           * Applicable functional interface.
           *
           * @note As written does NOT handle an arbitrary number of
           * parameters; should be rewritten as if it were defined
           * with (+ first . rest) so one parameter is required and
           * the remainder are captured in the list rest. True for
           * all of the arithmetic built-ins.
           */
          entry(Symbol("+"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> new Number(
                      actualParameterCons.nth(0).toNumber().value() + actualParameterCons.nth(1).toNumber().value()))),

          // -, *, /, % ...
          // Implemented all of the arithmetic built-ins.
          /**
           * Subtraction operation: Returns first number minus second number
           */
          entry(Symbol("-"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> new Number(
                      actualParameterCons.nth(0).toNumber().value() - actualParameterCons.nth(1).toNumber().value()))), 

          /**
           * Multiplication operation: Returns first number times second number
           */
          entry(Symbol("*"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> new Number(
                      actualParameterCons.nth(0).toNumber().value() * actualParameterCons.nth(1).toNumber().value()))), 

          /**
           * Division operation: Returns first number divided by second number
           */
          entry(Symbol("/"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> new Number(
                      actualParameterCons.nth(0).toNumber().value() / actualParameterCons.nth(1).toNumber().value()))), 

          /**
           * Modulo operation: Returns remainder when first number divided by second number
           */
          entry(Symbol("%"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> new Number(
                      actualParameterCons.nth(0).toNumber().value() % actualParameterCons.nth(1).toNumber().value()))),

          /**
           * Less than or equal comparison: Returns true if first number <= second number
           */
          entry(Symbol("<="),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> (actualParameterCons.nth(0).toNumber().value() <= actualParameterCons.nth(1).toNumber().value()) ? Boolean.True : Boolean.False)),

          /**
           * Greater than or equal comparison: Returns true if first number >= second number
           */
          entry(Symbol(">="),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> (actualParameterCons.nth(0).toNumber().value() >= actualParameterCons.nth(1).toNumber().value()) ? Boolean.True : Boolean.False)),

          /**
           * Less than comparison: Returns true if first number < second number
           */
          entry(Symbol("<"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> (actualParameterCons.nth(0).toNumber().value() < actualParameterCons.nth(1).toNumber().value()) ? Boolean.True : Boolean.False)),

          /**
           * Greater than comparison: Returns true if first number > second number
           */
          entry(Symbol(">"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> (actualParameterCons.nth(0).toNumber().value() > actualParameterCons.nth(1).toNumber().value()) ? Boolean.True : Boolean.False)), 
  

          entry(Symbol("not"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> {
                    try {
                      return (actualParameterCons.nth(0).toBoolean().truthValue()) ? Boolean.False : Boolean.True;
                    } catch (SchemeTypeException ste) {
                      return Boolean.False;
                    }
                  })),

          /**
           * and/or are SHORT-CIRCUIT and therefore must be special forms.
           */
          entry(Symbol("and"),
              Callable(Callable::id,
                  (callingEnv, actualParameterCons) -> {
                    try {
                      return actualParameterCons.nth(0).eval(callingEnv).toBoolean().truthValue()
                          ? actualParameterCons.nth(1).eval(callingEnv)
                          : Boolean.False;
                    } catch (SchemeTypeException ste) {
                      return actualParameterCons.nth(1).eval(callingEnv);
                    }
                  })),
          entry(Symbol("or"),
              Callable(Callable::id,
                  (callingEnv, actualParameterCons) -> {
                    SExp lhs = actualParameterCons.nth(0).eval(callingEnv);
                    try {
                      return lhs.toBoolean().truthValue() ? Boolean.True : actualParameterCons.nth(1).eval(callingEnv);
                    } catch (SchemeTypeException ste) {
                      return lhs;
                    }
                  })),

          entry(Symbol("null?"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> Cons.cons().equals(actualParameterCons.nth(0)) ? Boolean.True
                      : Boolean.False)),
          entry(Symbol("zero?"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> (actualParameterCons.nth(0).toNumber().value() == 0)
                      ? Boolean.True
                      : Boolean.False)),

          /**
           * Numeric comparison built-ins
           *
           */
          entry(Symbol("="),
              Callable(Callable::evalAll,
                  (callingEnv,
                      actualParameterCons) -> (actualParameterCons.nth(0).toNumber().value() == actualParameterCons
                          .nth(1).toNumber().value()) ? Boolean.True : Boolean.False)),
          // <, <=, >, >=, ...

          /**
           * Should have eq? eqv? equal? here...
           */
          entry(Symbol("eq?"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> actualParameterCons.nth(0).equals(actualParameterCons.nth(1)) ? Boolean.True : Boolean.False)),  
          entry(Symbol("eqv?"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> actualParameterCons.nth(0).equals(actualParameterCons.nth(1)) ? Boolean.True : Boolean.False)),
          entry(Symbol("equal?"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> actualParameterCons.nth(0).equals(actualParameterCons.nth(1)) ? Boolean.True : Boolean.False)),  

          /**
           * car, cdr, cons built-ins
           * Evaluate all parameters, apply the action.
           */
          entry(Symbol("car"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> actualParameterCons.car().toCons().car())),
          entry(Symbol("cdr"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> actualParameterCons.car().toCons().cdr())),
          entry(Symbol("cons"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> Cons.cons(actualParameterCons.nth(0),
                      actualParameterCons.nth(1)))),

          /**
           * Our print routines return an empty list; this is non-standard
           */
          entry(Symbol("print"),
              Callable(Callable::evalAll,
                       (callingEnv, actualParameterCons) -> {
                         System.out.printf("%s", actualParameterCons.car());
                         return Cons.cons();
                       })),
          entry(Symbol("println"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> {
                    System.out.printf("%s\n", actualParameterCons.car());
                    return Cons.cons();
                  })),
          entry(Symbol("newline"),
              Callable(Callable::evalAll,
                  (callingEnv, actualParameterCons) -> {
                    System.out.println();
                    return Cons.cons();
                  })),

          // ===============================================================
          // SPECIAL FORMS (but for and/or)
          // ===============================================================

          /**
           * You're going to have to write Callable::applyLambda
           * before this compiles.
           */
          entry(Symbol("lambda"),
              Callable(Callable::id,
                  (callingEnv, actualParameterCons) -> {
                      // First element should be parameter list
                      Cons formalParams = actualParameterCons.car().toCons();
                      // Rest is the body
                      Cons body = actualParameterCons.cdr().toCons();
                      return new CompoundProcedure(callingEnv, formalParams, body);
                  })),
          
          // entry(Symbol("define"),
          //     Callable(Callable::id,
          //         (callingEnv, actualParameterCons) -> {
          //           return callingEnv.define((Symbol) actualParameterCons.car(),
          //               actualParameterCons.cdr().toCons().car().eval(callingEnv));
          //         })),

          /**
           * Define a new variable or function.
           * If the first argument is a symbol, it is a variable definition.
           * If the first argument is a list, it is a function definition.
           */
          entry(Symbol("define"),
              Callable(Callable::id,
                  (callingEnv, actualParameterCons) -> {
                    SExp firstArg = actualParameterCons.car();
                    try {
                      // Try to cast to Symbol - if it works, this is a variable definition
                      Symbol symbol = firstArg.toSymbol();
                      return callingEnv.define(symbol,
                          actualParameterCons.cdr().toCons().car().eval(callingEnv));
                    } catch (SchemeTypeException ste) {
                      // If not a symbol, try to handle as function definition
                      try {
                        Cons functionDef = firstArg.toCons();
                        Symbol functionName = functionDef.car().toSymbol();
                        Cons params = functionDef.cdr().toCons();
                        Cons body = actualParameterCons.cdr().toCons();
                        
                        // Create lambda expression: (lambda params body)
                        Cons lambdaExpr = Cons.list(
                            new Symbol("lambda"),
                            params,
                            body.car()
                        );
                        
                        // Define the function name to be the evaluated lambda
                        return callingEnv.define(functionName, lambdaExpr.eval(callingEnv));
                      } catch (SchemeTypeException e) {
                        throw new SchemeTypeException("define expects either (define symbol value) or (define (name params...) body...)");
                      }
                    }
                  })),

          entry(Symbol("if"),
              Callable(Callable::id,
                  (callingEnv, actualParameterCons) -> {
                    return (actualParameterCons.nth(0).eval(callingEnv)).truthValue()
                        ? actualParameterCons.nth(1).eval(callingEnv)
                        : actualParameterCons.nth(2).eval(callingEnv);
                  })),

          entry(Symbol("quote"),
              Callable(Callable::id,
                  (callingEnv, actualParameterCons) -> actualParameterCons.car()))));

  /**
   * Syntactic Sugar: wrap the "new" operator inside a method call
   * @param name the name of the Symbol to create
   * @return a new Symbol AST object with the given name
   */
  private static Symbol Symbol(String name) {
    return new Symbol(name);
  }

  /**
   * Syntactic Sugar: wrap the "new" operator to make literal map prettier
   * @param actualParameterEvalStrategy {@link
   * ast.Callable.Argmorgrifier} to prepare actual parameters for
   * passing to a {@link Callable}.
   * @param applicationFunction {@link ast.Callable.Applicable}
   * function that does the actual function operation for the {@link
   * Callable}
   * @return a new {@link Callable} with given field values
   */
  private static Callable Callable(
      Callable.Argmorgrifier actualParameterEvalStrategy,
      Callable.Applicable applicationFunction) {
    return new Callable(actualParameterEvalStrategy, applicationFunction);
  }
}
