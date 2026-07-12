package echo;

import java.util.Iterator;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Scanner;

import io.SansCommentFilterReader;

public class Echo
  implements EchoInterface {
  private LineNumberReader lnr;
  private String readAheadLine;

  public Echo(Reader inner) {
    Reader sansComment = new SansCommentFilterReader(inner);
    lnr = new LineNumberReader(sansComment, 1);
  }

  public String next() {
    hasNext();
    String returningNext = readAheadLine;
    readAheadLine = null;
    return returningNext;
  }

  public boolean hasNext() {
    try {
      if (readAheadLine == null)
        readAheadLine = lnr.readLine();
    } catch (IOException io) {
      readAheadLine = null;
    }
    return readAheadLine != null;
  }

  public int lineNumber() {
    return lnr.getLineNumber();
  }
}
