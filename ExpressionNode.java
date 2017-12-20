
public class ExpressionNode {

  protected static final double
    scanTermProb = 0.5,
    constTermProb = 0.05,
    genTermProb = 0.45,
    termProb[] = {
      scanTermProb,
      constTermProb,
      genTermProb
    },

    unaryFuncProb = 0.2,
    binaryFuncProb = 0.5,
    ternaryFuncProb = 0.1,
    quaternaryFuncProb = 0.2
    funcProb[] = {
      unaryFuncProb,
      binaryFuncProb,
      ternaryFuncProb,
      quaternaryFuncProb
    },

    terminalProb = 0.3,
    functionProb = 0.7,
    prob_cross_term = 0.1,
    prob_cross_func = 0.9;

  protected int depth, arity;
  protected static final int maxDepth = 6;
  protected static final int minDepth = 3;

  protected Random rng = new Random();

  protected ExpressionNode[] children;
  protected String expression;

  public ExpressionNode(depth) {
    this.depth = depth;
  }

  public String compose() {
    String composed = expression;

    for (int i = 0; i < this.arity; i++) {
      composed = composed.replaceFirst("&"+i, children[i].compose());
    }

    composed = "("+composed+")";

    return composed;
  }

  public void evolve() {
    setArity();
    assignExpression();
  }

  public void setArity() {
    double randno = rng.nextDouble();
    if (minDepth > depth || (rng.nextDouble() < functionProb && depth < maxDepth)) {
      for (int i = 0; i < funcProb.length; i++) {
        randno -= funcProb[i];
        if (randno <= 0) {
          arity = i + 1;
          break;
        }
      }
    } else {
        arity = 0;
    }
    children = new ExpressionNode[arity];
  }

  public void assignExpression() {
    double randno  = rng.nextDouble();
    if (arity == 0) {
      for (int i = 0; i < termProb; i++) {
        randno -= termProb[i];
        if (randno <= 0) {
          expression = functionTerminalSet[0][i][rng.nextInt(functionTerminalSet[0][i].length)];
          break;
        }
      }
    } else {
      expression = functionTerminalSet[arity][rng.nextInt(functionTerminalSet[arity].length)];
      for (int i = 0; i < children.length; i++) {
        children[i] = new ExpressionNode(depth + 1);
        children[i].evolve(depth + 1);
      }
    }
  }

  public int getNodeCount() {
    int nodeCount = 1;
    for (int i = 0; i < arity; i++) {
      nodeCount += children[i].getNodeCount()
    }
    return nodeCount;
  }

  public ExpressionNode cross(ExpressionNode tree) {
    ExpressionNode child = copy();
    if (prob_cross_func < rng.nextDouble()) {
      child = child.replace(tree.getSubTree(false));
    } else {
      child = child.replace(tree.getSubTree(true));
    }
  }

  public ExpressionNode copy() {
    ExpressionNode copy = new ExpressionNode(depth);
    copy.arity = arity;
    copy.children = new ExpressionNode[arity];
    copy.expression = expression;
    for (int i = 0; i < arity; i++) {
      copy.children[i] = children[i].clone();
    }
    return copy;
  }

  public ExpressionNode getSubTree(Boolean isTerminal) {
    int nodeCount = getNodeCount();
    if (!isTerminal) {
      return getNode(rng.nextInt(nodeCount));
  }

  public ExpressionNode getNode(int nodeNum) {

  }

  replace() {

  }

  protected static final String[] onScannedRobotTerminals = {
    "e.getBearing()", // in double degrees (-180 <= getBearing() < 180)
    "e.getDistance()", // in double pixels
    "e.getEnergy()", // in double
    "e.getHeading()", // in double degrees (0 <= getHeading() < 360)
    "e.getVelocity()" // in double pixels/turn
  };

  protected static final String[] constantTerminals = {
    "Math.PI",
    "Math.random()",
    "0"
  };

  protected static final String[] generalTerminals = {
    "getBattleFieldHeight()", // in double pixels
    "getBattleFieldWidth()", // in double pixels
    "getEnergy()", // in double
    "getGunCoolingRate()", // in double units/turn
    "getGunHeading()", // in double degrees (0 <= getGunHeading() < 360)
    "getHeading()", // in double degrees (0 <= getHeading() < 360)
    "getOthers()", // in int amount of opponents remaining in round
    "getRadarHeading()", // in double degrees (0 <= getRadarHeading() < 360)
    "getVelocity()", // in double pixels/turn
    "getX()", // in double pixels
    "getY()", // in double pixels
  };

  protected static final String[][] terminalSet = {
    onScannedRobotTerminals,
    constantTerminals,
    generalTerminals
  };

  protected static final String[] unaryFunctions = {
    "Math.sin(&1)",
    "Math.cos(&1)",
    "Math.asin(&1)",
    "Math.acos(&1)",
    "Math.abs(&1)",
    "-1 * &1"
  };

  protected static final String[] binaryFunctions = {
    "Math.min(&1, &2)",
    "Math.max(&1, &2)",
    "&1 + &2",
    "&1 - &2",
    "&1 * &2",
    "&1 / &2"
  };

  protected static final String[] ternaryFunctions = {
    "&1 > 0 ? &2 : &3"
  };

  protected static final String[] quaternaryFunctions = {
    "&1 > &2 ? &3 : &4"
  };

  protected static final String[][][] functionTerminalSet = {
    terminalSet,
    unaryFunctions,
    binaryFunctions,
    ternaryFunctions,
    quaternaryFunctions
  };

}
