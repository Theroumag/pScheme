package ast;

import ast.exception.SchemeException;
import ast.exception.SchemeTypeException;
import env.Env;

/**
 * Representing a Scheme FUNCTION or SPECIAL FORM. Constructed to keep
 * together an Argmorgrifier (thanks Calvin and Hobbes) and an
 * Applicable.
 *
 * Argmorgrifier takes the current environment and a Cons, the list of
 * _actual_ parameters. It returns a Cons with some/all of the
 * elements in the actual parameters evaluated. The two standard
 * Argmorgrifiers are
 *   - id : identity transformation returns the Cons unmodified. This
 *     is for the use of SPECIAL FORMS that may or may not evaluate
 *     all actual parameter expressions.
 *   - evalAll : returns Cons with results of evaluating each actual
 *     parameter in the given environment.
 */
public class Callable extends SExp {
  // Definitions of Argmorgrifier and Applicable as
  // FunctionalInterfaces (interfaces with a single public
  // method). Java provides simplified instantiation syntax.

  /**
   * Argmorgrifier modifies a Cons in an Env
   */
  @FunctionalInterface
  public static interface Argmorgrifier {
    /**
     * @param env - current environment if any elements in args need
     * to be evaluated
     * @param args - list of actual arguments to a function or special
     * form.
     * @returns list of actual arguments after modification (if any is
     * done)
     */
    Cons morgrify(Env env, Cons args) throws SchemeException;
  }

  /**
   * Applicable applies some "code" (Java or Scheme) to arguments.
   */
  @FunctionalInterface
  public static interface Applicable {
    /**
     * @param env  - current environment if any elements in args need
     *             to be evaluated
     * @param args - list of actual arguments to a function or special
     *             form.
     * @return results of applying the code to the arguments within
     *         the given Env
     * @throws SchemeTypeException if apply applies a function to a
     *           list containing the wrong type(s).
     */
    SExp apply(Env env, Cons args) throws SchemeException;
  }

  // the two parts of a callable
  private final Argmorgrifier argmorgrifier;
  private final Applicable applicable;

  /**
   * Construct a new Callable combining given bits.
   * @param argmorgrifier - the function for evaluating the actual
   * parameters when this callable is applied to arguments.
   * @param applicable - the function applied to the arguments after
   * they are morgrified.
   */
  public Callable(Argmorgrifier argmorgrifier, Applicable applicable) {
    this.argmorgrifier = argmorgrifier;
    this.applicable = applicable;
  }

  // -----------------------------------------------------------------
  // Standard Argmorgrifiers
  // -----------------------------------------------------------------

  /**
   * Identity morgrifcation
   * Return the same argument list w/o evaluating any part of it
   * @param env - current environment for evaluation of arguments
   * @param args - list of arguments to some Callable
   * @return args, unchanged
   */
  public static Cons id(Env env, Cons args) {
    return args;
  }

  /**
   * Evaluate all elements morgrification
   * Return list of evaluated arguments.
   *
   * @param env  - current environment for evaluation of arguments
   * @param args - list of arguments to some Callable
   * @return args, each element evaluated in the given env
   */
  public static Cons evalAll(Env env, Cons args) throws SchemeException {
    return args.evalCons(env);
  }


  /**
   * Construct a CompoundProcedure with env, formal argument list, and body.
   */
  public static SExp applyLambda(Env env, Cons args) throws SchemeException {
    // keyword was lambda. Now have a list of parameters and a body. Use nth to
    // extract
    Cons formalParameterList = args.car().toCons();
    Cons body = args.cdr().toCons();
    return new CompoundProcedure(env, formalParameterList, body);
  }

  /**
   * Apply this Callable to an actual parameter list.
   *
   * Convert argument list to a Cons.
   * Apply the Argmorgrifier to get the MODIFIED argument list
   * Call apply to get the result. If it is a built-in, apply is a
   * Java function somewhere. If it is a lambda, apply does what
   * CompoundProcedure says to do.
   *
   * @param env the calling environment
   * @param unmodifiedActualArguments the un-modified argument
   * list...as written does not deal with varargs.
   * @return whatever the stored applicable function does with the env
   *         and modified argument list
   */
  @Override
  public SExp apply(Env env, SExp unmodifiedActualArguments) throws SchemeException {
    Cons actualArgumentList = unmodifiedActualArguments.toCons();
    Cons modifiedActualArgumentList = argmorgrifier.morgrify(env, actualArgumentList);
    SExp result = applicable.apply(env, modifiedActualArgumentList);
    return result;
  }
}
