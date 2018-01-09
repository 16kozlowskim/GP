public class Test {

  public static void main(String[] args) {

    Runnable[] task = new Runnable[10];
    for (int i = 0; i < task.length; i++) {
      task[i] = () -> {
          String threadName = Thread.currentThread().getName();
          System.out.println("Hello " + threadName);
      };
      new Thread(task[i]).start();
    }
    System.out.println("Done!");
  }


}
