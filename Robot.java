public class Robot {

  static final int genFunctNum = 5;

  Random rng = new Random();

  ExpressionNode[] root;
  int generation;
  String name;
  int ID;
  ArrayList<ArrayList<ExpressionNode>> tree;

  static final double
    prob_cross_term = 0.1,
    prob_cross_func = 0.9;

  public Robot(int ID, int generation) {
    root = new ExpressionNode[genFunctNum];
    this.generation = generation;
    this.ID = ID;
    name = "Robot_ID" + ID + "_Gen" + generation;
    tree = new ArrayList<ArrayList<ExpressionNode>>();
  }

  public void initialize() {
    for (int i = 0; i < genFunctNum; i++) {
      root[i] = new ExpressionNode(0);
      root[i].evolve();
    }
  }

  public Robot crossOver(Robot robot, int ID) {
    Robot child = new Robot(ID, generation + 1);

    for (int i = 0; i < root.length; i++) {
      child.root[i] = root[i].copy();
      child.tree.add(child.root[i].assemble(child.root[i]));
      robot.tree.add(robot.root[i].assemble(robot.root[i]));

      Boolean isTerminal1 = rng.nextDouble() < prob_cross_func ? true : false;
      Boolean isTerminal2 = rng.nextDouble() < prob_cross_func ? true : false;

      ExpressionNode a = child.root[i].getSubTree(isTerminal1);
      do {
        ExpressionNode b = robot.root[i].getSubTree(isTerminal2);
      } while (2*a.treeSize() + 1 < b.treeSize());

      ExpressionNode replacement = b.copy();

      int j;
      for (j = 0; j < robot.tree.get(i).size(); j++) {
        if (robot.tree.get(i).get(j) == b) break;
      }
      int depth = robot.tree.get(i).get(j).depth;

      for (j-1; j > j-5; j--) {
        if (robot.tree.get(i).get(j).depth != depth) break;
      }

      for (int n = 0; n < robot.tree.get(i).get(j).arity; n++) {
        if (robot.tree.get(i).get(j).children[n] == b) {
          robot.tree.get(i).get(j).children[n] = replacement;
        }
      }
      replacement.fixDepths(robot.tree.get(i).get(j).depth - replacement.depth + 1);
    }
  }

  public Robot mutate() {
    
  }

  public void createSourceCode() {
    String[] geneticSource = new String[genFunctNum];
    for (int i = 0; i < genFunctNum; i++) {
      geneticSource[i] = root[i].compose();
    }
    String sourceCode =
      "package u1624396;" +
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
      "\n    ahead(" + geneticSource[0] + ")" +
      "\n    turnRadarLeft(" + geneticSource[1] + ")" +
      "\n    turnLeft(" + geneticSource[2] + ")" +
      "\n    turnGunLeft(" + geneticSource[3] + ")" +
      "\n    fire(" + geneticSource[4] + ")" +
      "\n  }" +
      "\n}";
  }

}
