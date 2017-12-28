import java.util.ArrayList;
import java.util.Random;
import java.io.FileWriter;

public class Robot {

  static final int genFunctNum = 5;

  static Random rng = new Random();

  ExpressionNode[] root;
  int generation;
  String name;
  int ID;
  ArrayList<ArrayList<ExpressionNode>> tree;
  Robot child;

  static final String pckg = "evolving";

  static final double
    crossTermProb = 0.1,
    crossFuncProb = 0.9,
    mutateTermProb = 0.2,
    mutateFuncProb = 0.8,
    mutationProb = 0.05,
    crossOverProb = 0.95;

  public Robot(int ID, int generation) {
    root = new ExpressionNode[genFunctNum];
    this.generation = generation;
    this.ID = ID;
    name = "Robot_ID_" + ID + "_Gen_" + generation;
    tree = new ArrayList<ArrayList<ExpressionNode>>();
  }

  public void update() {
    generation++;
    name = "Robot_ID_" + ID + "_Gen_" + generation;
  }

  public void initialize() {
    for (int i = 0; i < genFunctNum; i++) {
      root[i] = new ExpressionNode(0);
      root[i].evolve(0);
    }
  }

  public Robot geneticOp(Robot robot, int ID) {
    child = new Robot(ID, generation + 1);

    for (int i = 0; i < root.length; i++) {
      if (rng.nextDouble() < crossOverProb)
        crossOver(robot, i);
      else
        mutate(i);
    }

    return child;
  }

  public void crossOver(Robot robot, int i) {

    child.root[i] = root[i].copy();
    child.tree.add(child.root[i].assemble(child.root[i]));
    robot.tree.add(robot.root[i].assemble(robot.root[i]));

    Boolean isTerminal1 = rng.nextDouble() < crossTermProb ? true : false;
    Boolean isTerminal2 = rng.nextDouble() < crossTermProb ? true : false;

    ExpressionNode a = child.root[i].getSubTree(isTerminal1);
    ExpressionNode b;

    do {
      b = robot.root[i].getSubTree(isTerminal2);
    } while (2*a.treeSize() + 1 < b.treeSize());

    ExpressionNode replacement = b.copy();

    int j;
    for (j = 0; j < child.tree.get(i).size(); j++) {
      if (child.tree.get(i).get(j) == a) break;
    }
    if (j == 0) {
      child.root[i] = replacement;
      System.out.println("Crossed Root at "+i);
    } else {

      int depth = child.tree.get(i).get(j).depth;

      for (; j > 0; --j) {
        if (child.tree.get(i).get(j).depth == depth - 1) break;
      }

      for (int n = 0; n < child.tree.get(i).get(j).arity; n++) {
        if (child.tree.get(i).get(j).children[n] == a) {
          child.tree.get(i).get(j).children[n] = replacement;
          break;
        }
      }
      replacement.fixDepths(child.tree.get(i).get(j).depth - replacement.depth + 1);
    }
    replacement.fixDepths(replacement.depth);
  }

  public void mutate(int i) {

    child.root[i] = root[i].copy();
    child.tree.add(child.root[i].assemble(child.root[i]));

    Boolean isTerminal = rng.nextDouble() < mutateTermProb ? true : false;

    ExpressionNode a = child.root[i].getSubTree(isTerminal);

    int j;
    for (j = 0; j < child.tree.get(i).size(); j++) {
      if (child.tree.get(i).get(j) == a) break;
    }

    if (j == 0) {
      child.root[i] = new ExpressionNode(0);
      child.root[i].evolve(0);
    } else {

      int depth = child.tree.get(i).get(j).depth;

      for (; j > 0; --j) {
        if (child.tree.get(i).get(j).depth == depth - 1) break;
      }

      for (int n = 0; n < child.tree.get(i).get(j).arity; n++) {
        if (child.tree.get(i).get(j).children[n] == a) {
          child.tree.get(i).get(j).children[n] = new ExpressionNode(child.tree.get(i).get(j).depth + 1);
          child.tree.get(i).get(j).children[n].evolve(child.tree.get(i).get(j).depth + 1);
          break;
        }
      }
    }
  }

  public String createSourceCode() {
    String[] geneticSource = new String[genFunctNum];
    for (int i = 0; i < genFunctNum; i++) {
      geneticSource[i] = root[i].compose();
    }
    String sourceCode =
      "package " + pckg + ";" +
      "\nimport robocode.*;" +
      "\npublic class " + name + " extends Robot {" +
      "\n  public void run() {" +
      "\n    while(true) {" +
      "\n      turnLeft(Double.POSITIVE_INFINITY);" +
      "\n      turnRadarLeft(Double.POSITIVE_INFINITY);" +
      "\n      turnGunLeft(Double.POSITIVE_INFINITY);" +
      "\n    }" +
      "\n  }" +
      "\n  public void onScannedRobot(ScannedRobotEvent e) {" +
      "\n" +
      "\n    ahead(" + geneticSource[0] + ");" +
      "\n" +
      "\n    turnRadarLeft(" + geneticSource[1] + ");" +
      "\n" +
      "\n    turnLeft(" + geneticSource[2] + ");" +
      "\n" +
      "\n    turnGunLeft(" + geneticSource[3] + ");" +
      "\n" +
      "\n    fire(" + geneticSource[4] + ");" +
      "\n" +
      "\n  }" +
      "\n}";
    return sourceCode;
  }

  public void createFile() {
    try {
      FileWriter fileWriter = new FileWriter("C:\\robocode\\robots\\evolving\\" + name + ".java");
      fileWriter.write(createSourceCode());
      fileWriter.close();
      Process p = Runtime.getRuntime().exec("javac -cp \"C:\\robocode\\libs\\robocode.jar;\" C:\\robocode\\robots\\evolving\\" + name + ".java");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }


}
