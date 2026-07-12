package ast;

import ast.exception.SchemeTypeException;
import org.junit.jupiter.api.Test;
import token.Token;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AtomTest {
    @Test
    void FromIntTokenTest() throws SchemeTypeException {
        Token.Type ttype = Token.Type.INTEGER;
        String lexeme = "10";
        Token token = new Token("fname", 1, 1, ttype, lexeme);

        Atom actual = Atom.fromToken(token);
        Atom expected = new Number(Integer.parseInt(lexeme));
        assertEquals(expected, actual);
    }

    @Test
    void FromSymbTokenTest() throws SchemeTypeException {
        Token.Type ttype = Token.Type.SYMBOL;
        String lexeme = "hello";
        Token token = new Token("fname", 1, 1, ttype, lexeme);

        Atom actual = Atom.fromToken(token);
        Atom expected = new Symbol(lexeme);
        assertEquals(expected, actual);
    }

    @Test
    void FromBeginTokenTest() throws SchemeTypeException {
        Token.Type ttype = Token.Type.BEGIN;
        String lexeme = "begin";
        Token token = new Token("fname", 1, 1, ttype, lexeme);

        Atom actual = Atom.fromToken(token);
        Atom expected = new Symbol(lexeme);
        assertEquals(expected, actual);
    }

    @Test
    void FromDefineTokenTest() throws SchemeTypeException {
        Token.Type ttype = Token.Type.DEFINE;
        String lexeme = "define";
        Token token = new Token("fname", 1, 1, ttype, lexeme);

        Atom actual = Atom.fromToken(token);
        Atom expected = new Symbol(lexeme);
        assertEquals(expected, actual);
    }

    @Test
    void FromIfTokenTest() throws SchemeTypeException {
        Token.Type ttype = Token.Type.IF;
        String lexeme = "if";
        Token token = new Token("fname", 1, 1, ttype, lexeme);

        Atom actual = Atom.fromToken(token);
        Atom expected = new Symbol(lexeme);
        assertEquals(expected, actual);
    }

    @Test
    void FromDefineMacroTokenTest() throws SchemeTypeException {
        Token.Type ttype = Token.Type.DEFINE_MACRO;
        String lexeme = "definemacro";
        Token token = new Token("fname", 1, 1, ttype, lexeme);

        Atom actual = Atom.fromToken(token);
        Atom expected = new Symbol(lexeme);
        assertEquals(expected, actual);
    }

    @Test
    void FromLambdaTokenTest() throws SchemeTypeException {
        Token.Type ttype = Token.Type.LAMBDA;
        String lexeme = "lambda";
        Token token = new Token("fname", 1, 1, ttype, lexeme);

        Atom actual = Atom.fromToken(token);
        Atom expected = new Symbol(lexeme);
        assertEquals(expected, actual);
    }

    @Test
    void FromSetTokenTest() throws SchemeTypeException {
        Token.Type ttype = Token.Type.SET_BANG;
        String lexeme = "set!";
        Token token = new Token("fname", 1, 1, ttype, lexeme);

        Atom actual = Atom.fromToken(token);
        Atom expected = new Symbol(lexeme);
        assertEquals(expected, actual);
    }

    @Test
    void FromQuoteTokenTest() throws SchemeTypeException {
        Token.Type ttype = Token.Type.QUOTE;
        String lexeme = "quote";
        Token token = new Token("fname", 1, 1, ttype, lexeme);

        Atom actual = Atom.fromToken(token);
        Atom expected = new Symbol(lexeme);
        assertEquals(expected, actual);
    }

    @Test
    void FromQuasiquoteTokenTest() throws SchemeTypeException {
        Token.Type ttype = Token.Type.QUASIQUOTE;
        String lexeme = "quasiquote";
        Token token = new Token("fname", 1, 1, ttype, lexeme);

        Atom actual = Atom.fromToken(token);
        Atom expected = new Symbol(lexeme);
        assertEquals(expected, actual);
    }

    @Test
    void FromUnquoteTokenTest() throws SchemeTypeException {
        Token.Type ttype = Token.Type.UNQUOTE;
        String lexeme = "unquote";
        Token token = new Token("fname", 1, 1, ttype, lexeme);

        Atom actual = Atom.fromToken(token);
        Atom expected = new Symbol(lexeme);
        assertEquals(expected, actual);
    }

    @Test
    void FromUnquoteSplicingTokenTest() throws SchemeTypeException {
        Token.Type ttype = Token.Type.UNQUOTE_SPLICING;
        String lexeme = "unquote-splicing";
        Token token = new Token("fname", 1, 1, ttype, lexeme);

        Atom actual = Atom.fromToken(token);
        Atom expected = new Symbol(lexeme);
        assertEquals(expected, actual);
    }
}
