package ast;

public class Error extends SExp {
  private String msg;
  public Error(String msg) {
    this.msg = msg;
  }


  public String toString() {
    return String.format("AST Error: %s", msg);
  }

  // equality only by identity
}
