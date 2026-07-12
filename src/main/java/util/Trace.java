package util;

import java.io.PrintStream;

/**
 * Trace/debug object supporting a conditional printf
 *
 * Prints or does not print tracing or debug messages. Has a varargs
 * printf, a "tracing level", and a set of constants for turning on
 * and off the printing.
 */
public class Trace {
  // Constant values use in Trace.set or conditional printf
  // to represent different subsystems to turn on/off tracing
  public static final int OFF = 0x00000000;
  public static final int IO = 0x00000001;
  public static final int TOKEN = 0x00000002;
  public static final int PARSE = 0x00000004;
  public static final int EVAL = 0x00000008;
  public static final int ALL = 0xffffffff;

  // the current tracing level; initialize to OFF
  private static int _trace = OFF;
  // where to print the trace; initialized to stdout
  // Resetting it permits logging to files
  private static PrintStream _out = System.out;

  /**
   * Brute-force turns on tracing for ALL subsystems.
   */
  public static void on() {
    set(ALL);
  }

  /**
   * Brute-force turns off tracing for ALL subsystems.
   *
   */
  public static void off() {
    set(OFF);
  }

  /**
   * Turn on tracing for subsystems encoded in trace
   *
   * @param trace set current tracing level to this value
   */
  public static void set(int trace) {
    _trace = trace;
  }

  /**
   * Set the output stream for trace messages
   *
   * @param out new PrintStream where messages go
   */
  public static void out(PrintStream out) {
    _out = out;
  }

  /**
   * Get the current output stream for trace messages
   *
   * @return the current PrintStream
   */
  public static PrintStream out() {
    return _out;
  }

  /**
   * Printf with a first parameter of a "trace" bit pattern.
   *
   * Printf if the given level has any bits in common with the _trace
   * field.
   * @param level what bits determine IF print generates output
   * @param format a printf format string
   * @param args the list of replacement items for replacing fields in
   * format
   * @note notice that this method FORWARDS the variable arguments to
   * PrintStream.printf.
   */
  public static void printf(int level, String format, Object... args) {
    if ((_trace & level) != OFF) {
      _out.printf(format, args);
    }
  }
}
