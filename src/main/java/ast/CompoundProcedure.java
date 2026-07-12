package ast;

import env.Env;
import util.Trace;
import static util.Trace.*;
/**
 * Subclass of Callable to hold a lambda
 * Constructed with the defining environment, the formal parameter
 * list, and the body of the function.
 */
public class CompoundProcedure extends Callable {
  /**
   * Constructor with env, formals, body.
   *
   */
  public CompoundProcedure(Env definingEnv, Cons formalParameterList, Cons body) {
    /*
     * Super constructor expects an argsmorgrifier and an apply function.
     *
     * Not a special form, so the argsmorgrifier is evalAll.
     *
     * The body of the apply is a closure...in Java. It has access to
     * the parameters that are in scope in this constructor. So it can
     * - create a new local Env with definingEnv as its parent
     * - define all the formal parameters in the local Env with their
     *   formal parameter values
     * - eval all the elements of the body with the local Env
     * - return the result of the LAST clause of the body
     */
    super(Callable::evalAll,
          (callingEnv, actualParameterList) -> {
          // as written does not handle dotted var-arg parameter list
            Trace.printf(EVAL, "  formal: %s\n  actual: %s\n  body: %s\n",
                         formalParameterList,
                         actualParameterList,
                         body);
            
            // Create new environment with defining environment as parent
            Env localEnv = new Env(definingEnv);
            
            // Bind formal parameters to actual parameters
            Cons formalParams = formalParameterList;
            Cons actualParams = actualParameterList;
            
            while (!formalParams.equals(Cons.cons())) {
                Symbol formalParam = (Symbol)formalParams.car();
                SExp actualParam = actualParams.car();
                localEnv.define(formalParam, actualParam);
                formalParams = formalParams.cdr().toCons();
                actualParams = actualParams.cdr().toCons();
            }
            
            // Evaluate each expression in the body, return last result
            SExp result = Cons.cons();
            Cons currentBody = body;
            while (!currentBody.equals(Cons.cons())) {
                result = currentBody.car().eval(localEnv);
                currentBody = currentBody.cdr().toCons();
            }
            
            return result;
          });
  }
}
