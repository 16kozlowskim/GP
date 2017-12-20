import java.util.*;
import java.lang.String;

public class test {
  public static void main(String[] args) {
    String l = "Math.min($1, $2)".replaceFirst("$1", "1");
    l = l.replaceFirst("$2", "1");
    /*for (int i=0; i < l.length; i++)
      System.out.println(l[i]);
      System.out.println(l.length);*/
    System.out.println(l);
  }
}
