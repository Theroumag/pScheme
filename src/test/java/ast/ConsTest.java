package ast;

import ast.exception.SchemeException;
import ast.exception.SchemeTypeException;
// import env.Env;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsTest {
    @Test
    void cons0Test() {
        Cons cons = Cons.cons();
        assertEquals(Cons.cons(), cons);
        assertEquals("()", cons.toString());
    }

    @Test
    void cons1Test() {
        Cons first = Cons.cons(new Symbol("a"));
        Cons second = Cons.cons(new Symbol("a"));
        assertEquals(first, second);
        assertEquals("(a)", first.toString());
    }

    @Test
    void cons2Test() {
        Cons first = Cons.cons(new Symbol("a"), new Symbol("b"));
        Cons second = Cons.cons(new Symbol("a"), new Symbol("b"));
        assertEquals(first, second);
        assertEquals("(a . b)", first.toString());
    }

    @Test
    void listOfListsTest() {
        Cons a = Cons.cons(Cons.cons(new Symbol("a")));
        Cons first = Cons.cons(a, Cons.cons(new Symbol("b")));
        Cons second = Cons.cons(a, Cons.cons(new Symbol("b")));
        assertEquals(first, second);
        assertEquals("(((a)) b)", first.toString());
    }


    @Test
    void listTest() {
        Cons expected = Cons.cons();
        Cons actual = Cons.list();
        assertEquals(expected, actual);

        expected = Cons.cons(new Symbol("aleph"));
        actual = Cons.list(new Symbol("aleph"));
        assertEquals(expected, actual);

        expected = Cons.cons(new Symbol("a"), Cons.cons(new Number(1), Cons.cons(new Symbol("qqq"), Cons.cons(new Symbol("b")))));
        actual = Cons.list(new Symbol("a"), new Number(1), new Symbol("qqq"), new Symbol("b"));
        assertEquals(expected, actual);

        Cons car = Cons.list(new Symbol("A"), new Symbol("A"), new Symbol("A"), new Symbol("A"), new Symbol("A"));
        Cons cadr = Cons.list(new Number(1), Cons.list(new Symbol("x")));
        Cons cddr = Cons.list(new Symbol("x"), new Symbol("y"), new Symbol("z"));
        expected = Cons.cons(car, Cons.cons(cadr, cddr));
        actual = Cons.list(Cons.list(new Symbol("A"), new Symbol("A"), new Symbol("A"), new Symbol("A"), new Symbol("A")),
                Cons.list(new Number(1), Cons.list(new Symbol("x"))),
                new Symbol("x"), new Symbol("y"), new Symbol("z"));
        assertEquals(expected, actual);
    }

    @Test
    void nthTest() throws SchemeException {
        Cons lst = Cons.list(Cons.list(new Symbol("A"), new Symbol("B"), new Symbol("C"), new Symbol("D"), new Symbol("E")),
                Cons.list(new Number(1), Cons.list(new Symbol("x"))),
                new Symbol("x"), new Symbol("y"), new Symbol("z"));
        SExp first = Cons.list(new Symbol("A"), new Symbol("B"), new Symbol("C"), new Symbol("D"), new Symbol("E"));
        SExp second = Cons.list(new Number(1), Cons.list(new Symbol("x")));

        assertEquals(first, lst.nth(0));
        assertEquals(second, lst.nth(1));
    }

    // @Test
    // void evaluateConsTest() throws SchemeException {
    //     Env env = new Env();
    //     Symbol plus = new Symbol("+");
    //     Symbol b = new Symbol("b");
    //     Symbol c = new Symbol("c");
    //     SExp thirteen = new Number(13);
    //     SExp twentyfive = new Number(25);
    //     env.put(b, thirteen);
    //     env.put(c, twentyfive);

    //     Cons exp = Cons.list(plus, b, c);

    //     Cons expected = Cons.cons(thirteen, Cons.cons(twentyfive));

    //     SExp actual = null;
    //     try {
    //         actual = exp.cdr().toCons().evalCons(env);
    //     } catch (ast.exception.SchemeException e) {
    //         e.printStackTrace();
    //     }

    //     assertEquals(expected, actual);
    // }

    @Test
    void simpleAppendTest() throws SchemeTypeException {
        List<SExp> lst = List.of(new Symbol("a"), new Symbol("b"), new Symbol("c"), new Symbol("d"));
        Cons actual = null;
        for (SExp exp : lst) {
          Cons tail = Cons.cons(exp);
          if (actual == null)
            actual = tail;
          else
            actual = Cons.append(actual, tail).toCons();
        }

        Cons expected = Cons.list(new Symbol("a"), new Symbol("b"), new Symbol("c"), new Symbol("d"));

        assertEquals(expected, actual);
    }

    @Test
    void appendTest() throws SchemeTypeException {
        // (+ (- 0 x) (+ 2 5))
        List<SExp> lst = List.of(new Symbol("+"), Cons.cons(new Symbol("-"), Cons.cons(new Number(0), Cons.cons(new Symbol("x")))),
                Cons.cons(new Symbol("+"), Cons.cons(new Number(2), Cons.cons(new Number(5)))));
        Cons actual = null;
        for (SExp exp : lst) {
          Cons tail = Cons.cons(exp);
          if (actual == null)
            actual = tail;
          else
            actual = Cons.append(actual, tail).toCons();
        }

        Cons expected = Cons.cons(
                new Symbol("+"),
                Cons.cons(
                        Cons.cons(
                                new Symbol("-"),
                                Cons.cons(
                                        new Number(0),
                                        Cons.cons(
                                                new Symbol("x"),
                                                Cons.cons()
                                        )
                                )
                        ),

                        Cons.cons(
                                Cons.cons(
                                        new Symbol("+"),
                                        Cons.cons(
                                                new Number(2),
                                                Cons.cons(
                                                        new Number(5),
                                                        Cons.cons()
                                                )
                                        )
                                ),
                                Cons.cons()
                        )
                )
        );
        assertEquals(expected, actual);
    }

}
