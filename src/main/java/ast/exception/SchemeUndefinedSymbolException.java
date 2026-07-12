package ast.exception;

public class SchemeUndefinedSymbolException
        extends SchemeException {
    public SchemeUndefinedSymbolException(String msg) {
        super(msg);
    }

    public SchemeUndefinedSymbolException(String msg, String symbolName) {
        super(String.format(msg, symbolName));
    }
}
