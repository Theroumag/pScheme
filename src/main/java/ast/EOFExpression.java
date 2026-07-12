package ast;

public class EOFExpression extends SExp {
    private static EOFExpression _eofexpression = new EOFExpression();

    private EOFExpression() {
    }

    public static EOFExpression eofExpression() {
        return _eofexpression;
    }

    public String toString() {
      return "#EOF";
    }

  // equality only by identity
}
