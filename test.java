public class Test {
  public static void main(String[] args) {
    Robot robot1 = new Robot(1, 1);
    robot1.initialize();
    robot1.createFile();
    Robot robot2 = new Robot(2, 1);
    robot2.initialize();
    robot2.createFile();

    Robot child = robot1.mutate(3);
    child.createFile();
  }
}
