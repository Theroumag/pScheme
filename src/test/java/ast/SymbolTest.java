package ast;

// import env.Env;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SymbolTest {
    @Test
    void makeASymbolTest() {
        Symbol grandpa = new Symbol("frank");
        Symbol grandma = new Symbol("ruth");
        Symbol zappa = new Symbol("frank");

        assertNotNull(grandpa);
        assertNotNull(grandma);
        assertNotEquals(grandpa, grandma);
        assertEquals(grandpa, zappa);
    }

    // @Test
    // void evalSymbolTest() {
    //     Env env = new Env();
    //     Symbol a = new Symbol("a");
    //     Symbol b = new Symbol("b");

    //     env.put(a, b);

    //     SExp actual = null;
    //     try {
    //         actual = a.eval(env);
    //     } catch (ast.exception.SchemeException e) {
    //         e.printStackTrace();
    //     }
    //     assertEquals(actual, b);
    // }
}
