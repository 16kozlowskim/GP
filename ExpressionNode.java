
public class ExpressionNode {

  static final double
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

  int depth, arity;
  static final int maxDepth = 6;
  static final int minDepth = 3;

  Random rng = new Random();

  ArrayList<ExpressionNode> assembled;

  ExpressionNode[] children;
  String expression;

  public ExpressionNode(depth) {
    this.depth = depth;
  }

  public String compose() {
    String composed = expression;

    for (int i = 0; i < arity; i++) {
      composed = composed.replaceFirst("&"+i, children[i].compose());
    }

    composed = "("+composed+")";

    return composed;
  }

  public void evolve() {
    setArity();
    assignExpression();
  }

  public ArrayList<ExpressionNode> assemble(ExpressionNode node) {
    assembled = new ArrayList<ExpressionNode>();
    assembled.add(node);
    for (int i = 0; i < arity; i++) {
      assembled.addAll(children[i].assemble(children[i]));
    }
    return assembled;
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
    ArrayList<ExpressionNode> terminals = new ArrayList<ExpressionNode>();
    if (isTerminal) {
      for (int i = 0; i < assembled.size(); i++) {
        if (assembled.get(i).arity == 0) {
          terminals.add(assembled.get(i));
        }
      }
      terminals.get(rng.nextInt());
    } else {

    }
  }

  public void replace() {

  }

  static final String[] onScannedRobotTerminals = {
    "e.getBearing()", // in double degrees (-180 <= getBearing() < 180)
    "e.getDistance()", // in double pixels
    "e.getEnergy()", // in double
    "e.getHeading()", // in double degrees (0 <= getHeading() < 360)
    "e.getVelocity()" // in double pixels/turn
  };

  static final String[] constantTerminals = {
    "Math.PI",
    "Math.random()",
    "0"
  };

  static final String[] generalTerminals = {
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

  static final String[][] terminalSet = {
    onScannedRobotTerminals,
    constantTerminals,
    generalTerminals
  };

  static final String[] unaryFunctions = {
    "Math.sin(&1)",
    "Math.cos(&1)",
    "Math.asin(&1)",
    "Math.acos(&1)",
    "Math.abs(&1)",
    "-1 * &1"
  };

  static final String[] binaryFunctions = {
    "Math.min(&1, &2)",
    "Math.max(&1, &2)",
    "&1 + &2",
    "&1 - &2",
    "&1 * &2",
    "&1 / &2"
  };

  static final String[] ternaryFunctions = {
    "&1 > 0 ? &2 : &3"
  };

  static final String[] quaternaryFunctions = {
    "&1 > &2 ? &3 : &4"
  };

  static final String[][][] functionTerminalSet = {
    terminalSet,
    unaryFunctions,
    binaryFunctions,
    ternaryFunctions,
    quaternaryFunctions
  };

}
