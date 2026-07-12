package ast;

import ast.exception.SchemeTypeException;
import token.Token;

import static token.Token.Type.*;

public class Atom extends SExp {

    public static Atom fromToken(Token token) throws SchemeTypeException {
        if (token.type == INTEGER) {
            return new Number(Integer.parseInt(token.lexeme));
        } else if (Token.isSymbolOrKeyword(token.type)) {
            return new Symbol(token.lexeme);
        }
        throw new SchemeTypeException(String.format("Attempt to extract Symbol from non-Symbol token: %s", token));
    }


}
