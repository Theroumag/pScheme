package driver;

import java.io.IOException;

public class StdinDriver {

  public static void main(String[] args) throws IOException{

    System.out.print("Press any key: ");
    int x = System.in.read(); //get next byte immediately}
    System.out.printf("x: %c\n", (char) x);
  }
}
