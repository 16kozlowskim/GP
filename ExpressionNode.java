import java.util.ArrayList;
import java.util.Random;

public class ExpressionNode {

  static final double scanRobotTermProb = 0.5,
    constTermProb = 0.05,
    genTermProb = 0.5;

  static final double[] termProb = {
      scanRobotTermProb,
      constTermProb,
      genTermProb
    };

  static final double hitByBulletTermProb = 0.25,
    constTermProb2 = 0.1,
    genTermProb2 = 0.65;

  static final double[] termProb2 = {
      hitByBulletTermProb,
      constTermProb2,
      genTermProb2
    };

  static final double hitRobotTermProb = 0.25,
    constTermProb3 = 0.1,
    genTermProb3 = 0.65;

  static final double[] termProb3 = {
      hitRobotTermProb,
      constTermProb3,
      genTermProb3
    };

  static final double eventTermProb[][] = {
      termProb,
      termProb2,
      termProb3
    };

  static final double unaryFuncProb = 0.2,
    binaryFuncProb = 0.5,
    ternaryFuncProb = 0.1,
    quaternaryFuncProb = 0.2;

  static final double[] funcProb = {
      unaryFuncProb,
      binaryFuncProb,
      ternaryFuncProb,
      quaternaryFuncProb
    };

  static final double terminalProb = 0.3,
    functionProb = 0.7;

  int depth, arity;
  static final int maxDepth = 5;
  static final int minDepth = 2;

  static Random rng = new Random();

  ArrayList<ExpressionNode> assembled;

  ExpressionNode[] children;
  String expression;

  public ExpressionNode(int depth) {
    this.depth = depth;
  }

  public void grow(int off, int val) {

    double randno;
    int index = -1;

    switch (val) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
        index = 0;
        break;
      case 5:
      case 6:
        index = 1;
        break;
      case 7:
      case 8:
        index = 2;
    }

    randno = rng.nextDouble();

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

    randno = rng.nextDouble();

    if (arity == 0) {
      for (int i = 0; i < termProb.length; i++) {
        randno -= eventTermProb[index][i];
        if (randno < 0) {
          if (i == 0) i = index;
          else i += 2;
          expression = functionTerminalSet[0][i][rng.nextInt(functionTerminalSet[0][i].length)];
          break;
        }
      }
    } else {
      expression = functionTerminalSet[arity][rng.nextInt(functionTerminalSet[arity].length)][0];
      for (int i = 0; i < arity; i++) {
        children[i] = new ExpressionNode(depth + 1);
        children[i].grow(off, val);
      }
    }
  }

  public ArrayList<ExpressionNode> assembleList(ExpressionNode node) {
    assembled = new ArrayList<ExpressionNode>();
    assembled.add(node);
    for (int i = 0; i < arity; i++) {
      assembled.addAll(children[i].assembleList(children[i]));
    }
    return assembled;
  }

  public String assembleExpression() {
    String composed = expression;

    for (int i = 0; i < arity; i++) {
      composed = composed.replaceFirst("&"+(i+1), children[i].assembleExpression());
    }

    composed = "("+composed+")";

    return composed;
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
    if ((treeSize() == 1) && !isTerminal) return getSubTree(true);
    ArrayList<ExpressionNode> nodes = new ArrayList<ExpressionNode>();
    if (isTerminal) {
      for (int i = 0; i < assembled.size(); i++) {
        if (assembled.get(i).arity == 0) {
          nodes.add(assembled.get(i));
        }
      }
    } else {
      for (int i = 0; i < assembled.size(); i++) {
        if (assembled.get(i).arity > 0) {
          nodes.add(assembled.get(i));
        }
      }
    }
    if (nodes.size() == 0) return getSubTree(true);

    ExpressionNode a = null;
    try {
      a = nodes.get(rng.nextInt(nodes.size()));
    } catch (Exception e) {
      System.out.println("size: " + nodes.size());
    }
    return a;
  }

  public ExpressionNode getSubTree(Boolean isTerminal, int treeSize) {
    if ((treeSize() == 1) && !isTerminal) return getSubTree(true, treeSize);
    int maxSize = (2 * treeSize) + 1;
    ArrayList<ExpressionNode> nodes = new ArrayList<ExpressionNode>();
    if (isTerminal) {
      for (int i = 0; i < assembled.size(); i++) {
        if (assembled.get(i).arity == 0) {
          nodes.add(assembled.get(i));
        }
      }
    } else {
      for (int i = 0; i < assembled.size(); i++) {
        if ((assembled.get(i).arity > 0) && (assembled.get(i).treeSize() <= maxSize)) {
          nodes.add(assembled.get(i));
        }
      }
    }
    if (nodes.size() == 0) return getSubTree(true);

    ExpressionNode a = null;
    try {
      a = nodes.get(rng.nextInt(nodes.size()));
    } catch (Exception e) {
      System.out.println("size2: " + nodes.size());
      System.out.println(assembled.size());
      System.out.println(isTerminal);
    }
    return a;
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
    {"&1 > 0 ? &2 : &3"},
    {"&1 < 0 ? &2 : &3"},
    {"&1 == 0 ? &2 : &3"}
  };

  static final String[][] quaternaryFunctions = {
    {"&1 > &2 ? &3 : &4"},
    {"&1 < &2 ? &3 : &4"},
    {"&1 == &2 ? &3 : &4"}
  };

  static final String[] onScannedRobotTerminals = {
    "e.getBearing()", // in double degrees (-180 <= getBearing() < 180)
    "e.getBearingRadians()",
    "e.getDistance()", // in double pixels
    "e.getEnergy()", // in double
    "e.getHeading()", // in double degrees (0 <= getHeading() < 360)
    "e.getHeadingRadians()",
    "e.getVelocity()" // in double pixels/turn
  };

  static final String[] onHitByBulletTerminals = {
    "e.getBearing()",
    "e.getBearingRadians()",
    "e.getHeading()",
    "e.getHeadingRadians()",
    "distanceToEnemy",
    "enemyHeading"
  };

  static final String[] onHitRobotTerminals = {
    "e.getBearing()",
    "e.getBearingRadians()",
    "e.getEnergy()",
    "distanceToEnemy",
    "enemyHeading"
  };

  static final String[] constantTerminals = {
    "Math.PI",
    "2 * Math.random() - 1",
    "0.01",
  };

  static final String[] generalTerminals = {
    "getBattleFieldHeight()", // in double pixels
    "getBattleFieldWidth()", // in double pixels
    "getEnergy()", // in double
    "getGunCoolingRate()", // in double units/turn
    "getGunHeading()", // in double degrees (0 <= getGunHeading() < 360)
    "getHeading()", // in double degrees (0 <= getHeading() < 360)
    "getRadarHeading()", // in double degrees (0 <= getRadarHeading() < 360)
    "getVelocity()", // in double pixels/turn
    "getX()", // in double pixels
    "getY()", // in double pixels
  };

  static final String[][] terminalSet = {
    onScannedRobotTerminals,
    onHitByBulletTerminals,
    onHitRobotTerminals,
    constantTerminals,
    generalTerminals
  };

  static final String[][][] functionTerminalSet = {
    terminalSet,
    unaryFunctions,
    binaryFunctions,
    ternaryFunctions,
    quaternaryFunctions
  };


}
