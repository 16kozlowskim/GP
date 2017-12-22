public class Robot {

  static final int genFunctNum = 5;

  Random rng = new Random();

  ExpressionNode[] geneticTree;
  int generation;
  String name;
  int ID;
  ArrayList<ArrayList<ExpressionNode>> tree;

  static final double
    prob_cross_term = 0.1,
    prob_cross_func = 0.9;

  public Robot(int ID, int generation) {
    geneticTree = new ExpressionNode[genFunctNum];
    this.generation = generation;
    this.ID = ID;
    name = "Robot_ID" + ID + "_Gen" + generation;
    tree = new ArrayList<ArrayList<ExpressionNode>>();
  }

  public void initialize() {
    for (int i = 0; i < genFunctNum; i++) {
      geneticTree[i] = new ExpressionNode(0);
      geneticTree[i].evolve();
    }
  }

  public Robot crossOver(Robot robot, int ID) {
    Robot child = new Robot(ID, generation + 1);

    for (int i = 0; i < geneticTree.length; i++) {
      child.geneticTree[i] = geneticTree[i].copy();
      child.tree.add(child.geneticTree[i].assemble(child.geneticTree[i]));
      robot.tree.add(robot.geneticTree[i].assemble(robot.geneticTree[i]));

      Boolean isTerminal1 = rng.nextDouble() < prob_cross_func ? true : false;
      Boolean isTerminal2 = rng.nextDouble() <  prob_cross_func ? true : false;



    }
  }

  public Robot mutate() {

  }

  public void createSourceCode() {
    String[] geneticSource = new String[genFunctNum];
    for (int i = 0; i < genFunctNum; i++) {
      geneticSource[i] = geneticTree[i].compose();
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
