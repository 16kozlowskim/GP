import java.util.ArrayList;
import java.util.Random;

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
    quaternaryFuncProb = 0.2,
    funcProb[] = {
      unaryFuncProb,
      binaryFuncProb,
      ternaryFuncProb,
      quaternaryFuncProb
    },

    terminalProb = 0.3,
    functionProb = 0.7;

  int depth, arity;
  static final int maxDepth = 4;
  static final int minDepth = 1;

  static Random rng = new Random();

  ArrayList<ExpressionNode> assembled;

  ExpressionNode[] children;
  String expression;

  public ExpressionNode(int depth) {
    this.depth = depth;
  }

  public String compose() {
    String composed = expression;

    for (int i = 0; i < arity; i++) {
      composed = composed.replaceFirst("&"+(i+1), children[i].compose());
    }

    composed = "("+composed+")";

    return composed;
  }

  public void evolve(int off) {
    setArity(off);
    assignExpression(off);
  }

  public ArrayList<ExpressionNode> assemble(ExpressionNode node) {
    assembled = new ArrayList<ExpressionNode>();
    assembled.add(node);
    for (int i = 0; i < arity; i++) {
      assembled.addAll(children[i].assemble(children[i]));
    }
    return assembled;
  }

  public void setArity(int off) {
    double randno = rng.nextDouble();
    if (((minDepth + off) > depth) || ((rng.nextDouble() < functionProb) && (depth < (maxDepth + off)))) {
      for (int i = 0; i < funcProb.length; i++) {
        randno -= funcProb[i];
        if (randno < 0) {
          arity = i + 1;
          break;
        }
      }
    } else {
        arity = 0;
    }
    children = new ExpressionNode[arity];
  }

  public void assignExpression(int off) {
    double randno  = rng.nextDouble();
    if (arity == 0) {
      for (int i = 0; i < termProb.length; i++) {
        randno -= termProb[i];
        if (randno < 0) {
          expression = functionTerminalSet[0][i][rng.nextInt(functionTerminalSet[0][i].length)];
          break;
        }
      }
    } else {
      expression = functionTerminalSet[arity][rng.nextInt(functionTerminalSet[arity].length)][0];
      for (int i = 0; i < arity; i++) {
        children[i] = new ExpressionNode(depth + 1);
        children[i].evolve(off);
      }
    }
  }

  public ExpressionNode copy() {
    ExpressionNode copy = new ExpressionNode(depth);
    copy.arity = arity;
    copy.children = new ExpressionNode[arity];
    copy.expression = expression;
    for (int i = 0; i < arity; i++) {
      copy.children[i] = children[i].copy();
    }
    return copy;
  }

  public ExpressionNode getSubTree(Boolean isTerminal) {
    ArrayList<ExpressionNode> nodes = new ArrayList<ExpressionNode>();
    if (isTerminal) {
      for (int i = 0; i < assembled.size(); i++) {
        if (assembled.get(i).arity == 0) {
          nodes.add(assembled.get(i));
        }
      }
    } else {
      for (int i = 0; i < assembled.size(); i++) {
        if (assembled.get(i).arity != 0) {
          nodes.add(assembled.get(i));
        }
      }
    }
    return nodes.get(rng.nextInt(nodes.size()));
  }

  public int treeSize() {
    return assembled.size();
  }

  public void fixDepths(int off) {
    depth += off;
    for (int i = 0; i < arity; i++) {
      children[i].fixDepths(off);
    }
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

  static final String[][] unaryFunctions = {
    {"Math.sin(&1)"},
    {"Math.cos(&1)"},
    {"Math.asin(&1)"},
    {"Math.acos(&1)"},
    {"Math.abs(&1)"},
    {"-1 * &1"},
    {"Math.toRadians(&1)"},
    {"Math.toDegrees(&1)"}
  };

  static final String[][] binaryFunctions = {
    {"Math.min(&1, &2)"},
    {"Math.max(&1, &2)"},
    {"&1 + &2"},
    {"&1 - &2"},
    {"&1 * &2"},
    {"&1 / &2"}
  };

  static final String[][] ternaryFunctions = {
    {"&1 > 0 ? &2 : &3"}
  };

  static final String[][] quaternaryFunctions = {
    {"&1 > &2 ? &3 : &4"}
  };

  static final String[][][] functionTerminalSet = {
    terminalSet,
    unaryFunctions,
    binaryFunctions,
    ternaryFunctions,
    quaternaryFunctions
  };

}
