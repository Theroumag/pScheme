package io;


import java.util.Queue;
import java.util.LinkedList;
import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * This class wraps a {@link Reader} and reads it while eliminating
 * end-of-line comments. End-of-line comments are defined to be the same
 * as Java end-of-line comments, beginning with a double slash and
 * extending to the end of the current line. The whole end-of-line
 * comment is treated as consisting of the end-of-line character (\n or
 * \r) found after the end of the comment.<br />
 * <br />
 * This class is modeled after the SourceReader class presented in
 * Harold, _Java I/O, 2E_, published by O'Reilly. The Unicode reading
 * methods were modified for the task at hand. Also, the variable c was
 * universally replaced with ch (this author (blad) is so pedantic).
 */
public class SansCommentFilterReader
  extends FilterReader {
  /** /** state flag; have we already seen the end of the stream? */
  private boolean endOfStream = false;

  /** the extra, read-ahead character (-1 if not useful) */

  private Queue<Integer> readAhead;

  /**
   * Construct a new {@link SansCommentFilterReader} wrapped around the
   * given {@link Reader}.
   *
   * @param  in  the {@link Reader} where this reader gets characters.
   */
  public SansCommentFilterReader(Reader in) {
    super(in);
    this.endOfStream = false;
    readAhead = new LinkedList<Integer>();
  }

  /**
   * Read one character while skipping end-of-line comments. An
   * end-of-line comment is offset with a pair of slashes, Java-style:
   * // this is comment
   *
   * @return  returns the character read
   */
  @Override
  public int read() throws IOException {
    int ch;// the character to return

    // was there already a character read ahead when dealing with a
    // slash? If so, return that character
    if (!readAhead.isEmpty())
      ch = readAhead.poll();
     else
      ch = in.read();

    // it can't start a comment so return it
    if ((ch != ';') && (ch != '#')) {
      return ch;
    }

    if ((ch == ';')) { // skip to eoln
      while ((ch != '\n') && (ch != '\r')) {// read to end-of-line
        ch = in.read();
      }
    } else {
      // is it the start of a comment?
      int readAheadCh = in.read();
      if (readAheadCh == '|') {
        boolean inComment = true;
        boolean atleastOneLine = false;
        while (inComment) {
          ch = in.read();
          while ((ch != '\n') && (ch != '\r') && (ch != '|')) {
            ch = in.read();
          }
          if (ch != '|') {
            readAhead.add(ch);
            atleastOneLine = true;
          } else {
            ch = in.read();
            inComment = ch != '#';
          }
        }
        if (!atleastOneLine)
          ch = ' ';
        else
          ch = readAhead.poll();
      } else { // not start of comment so save char and return
        readAhead.add(readAheadCh);
      }
    }
    return ch;
  }

  /**
   * Read length characters into text starting at offset. Written to use
   * {@link #read()} so only that method need know anything about
   * comments. Do Not Repeat Yourself.
   *
   * @param   text    character buffer where characters are to be placed
   * @param   offset  the offset into text where characters are to be
   *                  saved
   * @param   length  number of characters desired
   *
   * @return  number of characters read or -1 if read is past end of
   *          input stream
   */
  @Override
  public int read(char[] text, int offset, int length)
    throws IOException {
    if (endOfStream) {
      return -1;// end already reached
    }

    int charCount = 0;
    for (int i = offset; i < (offset + length); i++) {
      int temp = this.read();
      if (temp == -1) {
        endOfStream = true;
        break;
      }
      text[i] = (char) temp;
      charCount++;
    }
    return charCount;
  }

  /**
   * Skip over the given number of characters. Characters are counted
   * after removal of comments
   *
   * @param   n  number of characters to skip over
   *
   * @return  the number of characters actually skipped over; -1 if skip
   *          is past end of input stream
   */
  @Override
  public long skip(long n) throws IOException {
    char[] chArray = new char[(int) n];
    int charCountSkipped = this.read(chArray);
    return charCountSkipped;
  }
}
