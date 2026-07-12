package env;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import ast.SExp;
import ast.Cons;
import ast.Symbol;
import ast.exception.SchemeUndefinedSymbolException;

class TestEnv {
  @Test
  public void testDefaultConstructor() {
    Env env = new Env();
    assertNotNull(env);
  }

  @Test
  public void testSimplePutLookup() {
    Env env = new Env();
    Symbol a = new Symbol("zebra");
    SExp b = new Symbol("jackrabbit");

    try {
      env.define(a, b);
      SExp actual = env.lookup(a);
      assertEquals(b, actual);
    } catch (SchemeUndefinedSymbolException u) {
      System.out.println("Error " + u.getMessage());
      u.printStackTrace();
    }
  }

  @Test
  public void testChainedLookup() {
    Symbol[] sym = {
      new Symbol("michael-jackson"),
      new Symbol("socrates"),
      new Symbol("throne"),
      new Symbol("kilo")
    };
    SExp[] sexp = {
      new Symbol("thriller"),
      new Symbol("athens"),
      Cons.cons(),
      Cons.list(new Symbol("alpha"), new Symbol("beta"), new Symbol("gamma"))
    };
    Symbol kingOfPop = sym[0];
    Symbol beatIt = new Symbol("beat-it");

    Env parent = new Env();
    parent.define(sym[0], sexp[0]);

    Env childA = new Env(parent);
    Env childB = new Env(parent);

    // skip 0 on purpose
    for (int i = 1; i < sym.length; i++) {
      childA.define(sym[i], sexp[i]);
      childB.define(sym[i], sexp[i]);
    }
    childB.define(kingOfPop, beatIt);

    try {
      for (int i = 0; i < sym.length; i++) {
        assertEquals(sexp[i], childA.lookup(sym[i]));
      }

      assertEquals(beatIt, childB.lookup(kingOfPop));
    } catch (SchemeUndefinedSymbolException u) {
      System.out.println("Error " + u.getMessage());
      u.printStackTrace();
    }

  }

  @Test
  public void testFailedLookup() {
    Symbol[] sym = {
      new Symbol("michael-jackson"),
      new Symbol("socrates"),
      new Symbol("throne"),
      new Symbol("kilo")
    };
    SExp[] sexp = {
      new Symbol("thriller"),
      new Symbol("athens"),
      Cons.cons(),
      Cons.list(new Symbol("alpha"), new Symbol("beta"), new Symbol("gamma"))
    };
    final Symbol[] missing = {
      new Symbol("none"),
      new Symbol("helicopter"),
      new Symbol("munchkin"),
      new Symbol("butterfly")
    };

    Env parent = new Env();
    parent.define(sym[0], sexp[0]);

    Env childA = new Env(parent);

    // skip 0 on purpose
    for (int i = 1; i < sym.length; i++) {
      childA.define(sym[i], sexp[i]);
    }

    for (int i = 0; i < missing.length; i++) {
      final Symbol unfoundSymbol = missing[i];
      Exception e = assertThrows(SchemeUndefinedSymbolException.class,
                                 () ->
                                 {
                                   childA.lookup(unfoundSymbol);
                                 }
                                );

      String actualMessage = e.getMessage();
      String expectedMessage = String.format("Lookup of \"%s\" failed", unfoundSymbol.symbol());
      assertTrue(actualMessage.contains(expectedMessage));
    }
  }

  @Test
  public void testFromMapConstructorAndEquals() {
    Env env = new Env();
    Symbol a = new Symbol("michael-jackson");
    SExp b = new Symbol("thriller");

    env.define(a, b);

    Map<Symbol, SExp> toCopy = new HashMap<>();
    toCopy.put(a, b);

    Env oEnv = new Env(toCopy);

    assertEquals(env, oEnv);
  }

  @Test
  public void testEquals() {
    Env parent = new Env();
    Symbol a = new Symbol("michael-jackson");
    SExp b = new Symbol("thriller");

    assertNotEquals("abc", parent);
    assertNotEquals(parent, "abc");
    assertEquals(parent, parent);

    parent.define(a, b);

    Env childA = new Env(parent);
    Env childB = new Env(parent);
    Symbol c = new Symbol("socrates");
    SExp d = Cons.list(new Symbol("alpha"), new Symbol("beta"), new Symbol("gamma"));

    childA.define(c, d);

    childB.define(c, d);
    childB.define(a, b); // break equality

    assertNotEquals(childA, childB);
    assertNotEquals(childB, childA);
  }

  @Test
  public void testMoreConstruction() {
    Env parent = new Env();
    Symbol a = new Symbol("michael-jackson");
    SExp b = new Symbol("thriller");

    parent.define(a, b);

    Env childA = new Env(parent);
    Env childB = new Env(parent);
    Symbol c = new Symbol("socrates");
    SExp d = Cons.list(new Symbol("alpha"), new Symbol("beta"), new Symbol("gamma"));

    childA.define(c, d);
    childB.define(c, d);

    assertEquals(childA, childB);
    assertEquals(childB, childA);
  }

}
