import java.util.ArrayList;
import java.util.Random;
import java.io.*;

public class Robot {

  static final int genFunctNum = 18;

  static Random rng = new Random();

  TreeNode[] root;
  int generation;
  String name;
  int ID;
  ArrayList<ArrayList<TreeNode>> tree;
  Robot child;

  static final double
    crossTermProb = 0.1,
    crossFuncProb = 0.9,
    mutateTermProb = 0.2,
    mutateFuncProb = 0.8,
    mutationProb = 0.05,
    crossOverProb = 0.95;

  public Robot(int ID, int generation) {
    root = new TreeNode[genFunctNum];
    this.generation = generation;
    this.ID = ID;
    name = "Robot_ID_" + ID + "_Gen_" + generation;
    tree = new ArrayList<ArrayList<TreeNode>>();
  }

  public Robot clone(int ID, int genIncrement) {
    Robot clone = new Robot(ID, this.generation + genIncrement);
    for (int i = 0; i < genFunctNum; i++) {
      clone.root[i] = this.root[i].copy();
    }
    return clone;
  }

  public void initialize() {
    for (int i = 0; i < genFunctNum; i++) {
      root[i] = new TreeNode(0);
      root[i].grow(0, i % 9);
    }
  }

  public Robot geneticOp(Robot robot, Robot robot2, int ID) {
    child = new Robot(ID, generation + 1);

    for (int i = 0; i < genFunctNum/2; i++) {
      if (rng.nextDouble() < crossOverProb)
        crossOver(robot, i);
      else
        mutate(i);
    }

    for (int i = genFunctNum/2; i < genFunctNum; i++) {
      if (rng.nextDouble() < crossOverProb)
        crossOver(robot2, i);
      else
        mutate(i);
    }

    return child;
  }

  public void crossOver(Robot robot, int i) {

    child.root[i] = root[i].copy();
    child.tree.add(child.root[i].assembleList(child.root[i]));
    robot.tree.add(robot.root[i].assembleList(robot.root[i]));

    Boolean isTerminal1 = rng.nextDouble() < crossTermProb ? true : false;
    Boolean isTerminal2 = rng.nextDouble() < crossTermProb ? true : false;

    TreeNode a = child.root[i].getSubTree(isTerminal1);
    TreeNode b = robot.root[i].getSubTree(isTerminal2, a.treeSize());

    TreeNode replacement = b.copy();

    int j;
    for (j = 0; j < child.tree.get(i).size(); j++) {
      if (child.tree.get(i).get(j) == a) break;
    }
    if (j == 0) {
      child.root[i] = replacement;
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
    child.tree.add(child.root[i].assembleList(child.root[i]));

    Boolean isTerminal = rng.nextDouble() < mutateTermProb ? true : false;

    TreeNode a = child.root[i].getSubTree(isTerminal);

    int j;
    for (j = 0; j < child.tree.get(i).size(); j++) {
      if (child.tree.get(i).get(j) == a) break;
    }

    if (j == 0) {
      child.root[i] = new TreeNode(0);
      child.root[i].grow(0, i % 9);
    } else {

      int depth = child.tree.get(i).get(j).depth;

      for (; j > 0; --j) {
        if (child.tree.get(i).get(j).depth == depth - 1) break;
      }

      for (int n = 0; n < child.tree.get(i).get(j).arity; n++) {
        if (child.tree.get(i).get(j).children[n] == a) {
          child.tree.get(i).get(j).children[n] = new TreeNode(child.tree.get(i).get(j).depth + 1);
          child.tree.get(i).get(j).children[n].grow(child.tree.get(i).get(j).depth + 1, i % 9);
          break;
        }
      }
    }
  }

  public String createSourceCode() {
    String[] geneticSource = new String[genFunctNum];
    for (int i = 0; i < genFunctNum; i++) {
      geneticSource[i] = root[i].assembleExpression();
    }
    String sourceCode =
      "package evolving;" +
      "\nimport robocode.*;" +
      "\npublic class " + name + " extends Robot {" +
      "\n" +
      "\n  private double distanceToEnemy;" +
      "\n  private double enemyBearingRadians;" +
      "\n  private double enemyHeadingRadians;" +
      "\n  private boolean state = true;" +
      "\n" +
      "\n  public void run() {" +
      "\n    while(true) {" +
      "\n      if (state) {" +
      "\n        state = !state;" +
      "\n        turnGunLeft(Double.POSITIVE_INFINITY);" +
      "\n      }" +
      "\n      else {" +
      "\n        state = !state;" +
      "\n        turnGunRight(Double.POSITIVE_INFINITY);" +
      "\n      }" +
      "\n    }" +
      "\n  }" +
      "\n" +
      "\n  public void onScannedRobot(ScannedRobotEvent e) {" +
      "\n" +
      "\n    state = !state;" +
      "\n    distanceToEnemy = e.getDistance();" +
      "\n    enemyHeadingRadians = e.getHeadingRadians();" +
      "\n    enemyBearingRadians = e.getBearingRadians();" +
      "\n" +
      "\n    if (getOthers() == 1) {" +
      "\n      turnGunLeft(" + geneticSource[0] + ");" +
      "\n" +
      "\n      fire(" + geneticSource[1] + ");" +
      "\n" +
      "\n      turnLeft(" + geneticSource[2] + ");" +
      "\n" +
      "\n      ahead(" + geneticSource[3] + ");" +
      "\n" +
      "\n      turnRadarLeft(" + geneticSource[4] + ");" +
      "\n    } else {" +
      "\n      turnGunLeft(" + geneticSource[9] + ");" +
      "\n" +
      "\n      fire(" + geneticSource[10] + ");" +
      "\n" +
      "\n      turnLeft(" + geneticSource[11] + ");" +
      "\n" +
      "\n      ahead(" + geneticSource[12] + ");" +
      "\n" +
      "\n      turnRadarLeft(" + geneticSource[13] + ");" +
      "\n    }" +
      "\n  }" +
      "\n" +
      "\n  public void onHitByBullet(HitByBulletEvent e) {" +
      "\n" +
      "\n    if (getOthers() == 1) {" +
      "\n      turnLeft(" + geneticSource[5] + ");" +
      "\n" +
      "\n      ahead(" + geneticSource[6] + ");" +
      "\n    } else {" +
      "\n      turnLeft(" + geneticSource[14] + ");" +
      "\n" +
      "\n      ahead(" + geneticSource[15] + ");" +
      "\n    }" +
      "\n  }" +
      "\n" +
      "\n  public void onHitRobot(HitRobotEvent e) {" +
      "\n" +
      "\n    if (getOthers() == 1) {" +
      "\n      turnLeft(" + geneticSource[7] + ");" +
      "\n" +
      "\n      ahead(" + geneticSource[8] + ");" +
      "\n    } else {" +
      "\n      turnLeft(" + geneticSource[16] + ");" +
      "\n" +
      "\n      ahead(" + geneticSource[17] + ");" +
      "\n    }" +
      "\n  }" +
      "\n}";
    return sourceCode;
  }
}
