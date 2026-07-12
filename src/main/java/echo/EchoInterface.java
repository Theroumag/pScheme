package echo;

import java.util.Iterator;

/**
 * Echo - String iterator over an input source.
 *
 * Implements Iterator<String> (rather than Iterable<String>): means
 * hasNext/next looks work.
 *
 */
public interface EchoInterface
  extends Iterator<String> {

  /**
   * Read the next line from the Echo.
   * @return next full line if one is available; null otherwise
   */
  public String next();

  /**
   * Is there a line left to get?
   * @return true if another line could be read; false otherwise
   */
  public boolean hasNext();

  /**
   * What line number (1-based) was the last line read?
   * @return line number of last call to next
   */
  public int lineNumber();
}
