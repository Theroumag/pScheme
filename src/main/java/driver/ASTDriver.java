package driver;

import token.Token;
import static token.Token.Type.*;

import ast.SExp;
import ast.Atom;
import ast.Boolean;
import ast.Cons;
import ast.EOFExpression;
import ast.Number;
import ast.Symbol;
import ast.exception.SchemeException;

import java.util.ArrayList;
import java.util.List;

public class ASTDriver {
  final static String programName = "REPL Driver %s.%s";
  final static String majorVersion = "0";
  final static String minorVersion = "3-AST";
  String startUpFileName;
  public ASTDriver() {
  }

  public void run() {
    List<SExp> sexp = new ArrayList<>();

    sexp.add(new Symbol("alpha"));

    sexp.add(new Number(101));

    sexp.add(Boolean.True);
    sexp.add(Boolean.False);

    try {
      // from Token
      sexp.add(Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "zulu")));
      sexp.add(Atom.fromToken(new Token("fakeFileName", 1, 1, SET_BANG, "set!")));

      sexp.add(Cons.list(Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "*")),
                         Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "n")),
                         Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "p"))));

      sexp.add(Cons.cons());
      sexp.add(Cons.cons(Cons.cons()));

      SExp quadDefinition =
        Cons.list(Atom.fromToken(new Token("fakeFileName", 1, 1, LAMBDA, "lambda")),
                  Cons.list(Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "x"))),
                  Cons.list(Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "+")),

                            Cons.list(Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "*")),
                                      Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "a")),
                                      Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "x")),
                                      Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "x"))),

                            Cons.list(Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "*")),
                                      Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "b")),
                                      Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "x"))),

                            Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "c"))
                            )
                  );
      SExp quad =
        Cons.append(
                    Cons.list(
                              Atom.fromToken(new Token("fakeFileName", 1, 1, DEFINE, "define")),

                              Cons.list(Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "quadratic")),
                                        Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "a")),
                                        Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "b")),
                                        Atom.fromToken(new Token("fakeFileName", 1, 1, SYMBOL, "c"))
                                        )
                              ),
                    quadDefinition);
      sexp.add(quad);
    } catch (SchemeException e) {
      System.out.println("Error " + e.getMessage());
      e.printStackTrace();
    }

    for (SExp exp : sexp) {
      System.out.printf("[%s] %s\n", exp.getClass().getSimpleName(), exp);
    }

  }

  public static void main(String[] args) {
    ASTDriver driver = new ASTDriver();
    driver.run();
  }

}
