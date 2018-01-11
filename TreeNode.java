import java.util.ArrayList;
import java.util.Random;

public class TreeNode {

  static String[] unaryFunctions = {
    "Math.sin(&1)",
    "Math.cos(&1)",
    "Math.tan(&1)",
    "Math.asin(&1)",
    "Math.acos(&1)",
    "Math.atan(&1)",
    "Math.abs(&1)",
    "-1 * &1",
    "Math.toRadians(&1)",
    "Math.toDegrees(&1)"
  };

  static String[] binaryFunctions = {
    "Math.min(&1, &2)",
    "Math.max(&1, &2)",
    "&1 + &2",
    "&1 - &2",
    "&1 * &2",
    "&1 / &2"
  };

  static String[] ternaryFunctions = {
    "&1 > 0 ? &2 : &3",
    "&1 < 0 ? &2 : &3",
    "&1 == 0 ? &2 : &3"
  };

  static String[] quaternaryFunctions = {
    "&1 > &2 ? &3 : &4",
    "&1 < &2 ? &3 : &4",
    "&1 == &2 ? &3 : &4"
  };

  static String[] onScannedRobotTerminals = {
    "e.getBearingRadians()",
    "e.getDistance()", // in double pixels
    "e.getEnergy()", // in double
    "e.getHeadingRadians()",
    "e.getVelocity()" // in double pixels/turn
  };

  static String[] onHitByBulletTerminals = {
    "e.getBearingRadians()",
    "e.getHeadingRadians()",
    "distanceToEnemy",
    "enemyHeadingRadians",
    "enemyBearingRadians"
  };

  static String[] onHitRobotTerminals = {
    "e.getBearingRadians()",
    "e.getEnergy()",
    "distanceToEnemy",
    "enemyHeadingRadians",
    "enemyBearingRadians"
  };

  static String[] constantTerminals = {
    "Math.PI",
    "2 * Math.random() - 1",
    "0.01"
  };

  static String[] generalTerminals = {
    "getEnergy()", // in double
    "getGunHeading()", // in double degrees (0 <= getGunHeading() < 360)
    "getHeading()", // in double degrees (0 <= getHeading() < 360)
    "getRadarHeading()", // in double degrees (0 <= getRadarHeading() < 360)
    "getVelocity()", // in double pixels/turn
    "getX()", // in double pixels
    "getY()" // in double pixels
  };

  static String[][] terminalSet = {
    onScannedRobotTerminals,
    onHitByBulletTerminals,
    onHitRobotTerminals,
    constantTerminals,
    generalTerminals
  };

  static String[][] functionSet = {
    unaryFunctions,
    binaryFunctions,
    ternaryFunctions,
    quaternaryFunctions
  };

  static double scanRobotTermProb = 0.5,
    constTermProb = 0.05,
    genTermProb = 0.5;

  static double[] termProb = {
      scanRobotTermProb,
      constTermProb,
      genTermProb
    };

  static double hitByBulletTermProb = 0.4,
    constTermProb2 = 0.05,
    genTermProb2 = 0.55;

  static double[] termProb2 = {
      hitByBulletTermProb,
      constTermProb2,
      genTermProb2
    };

  static double hitRobotTermProb = 0.35,
    constTermProb3 = 0.05,
    genTermProb3 = 0.60;

  static double[] termProb3 = {
      hitRobotTermProb,
      constTermProb3,
      genTermProb3
    };

  static double eventTermProb[][] = {
      termProb,
      termProb2,
      termProb3
    };

  static double unaryFuncProb = 0.2,
    binaryFuncProb = 0.5,
    ternaryFuncProb = 0.1,
    quaternaryFuncProb = 0.2;

  static double[] funcProb = {
      unaryFuncProb,
      binaryFuncProb,
      ternaryFuncProb,
      quaternaryFuncProb
    };

  static double terminalProb = 0.3,
    functionProb = 0.7;

  int depth, paramNum;
  static int maxDepth = 5;
  static int minDepth = 2;

  static Random rng = new Random();

  ArrayList<TreeNode> assembled;

  TreeNode[] children;
  String expression;

  public TreeNode(int depth) {
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
          paramNum = i + 1;
          break;
        }
      }
    } else {
        paramNum = 0;
    }
    children = new TreeNode[paramNum];

    randno = rng.nextDouble();

    if (paramNum == 0) {
      for (int i = 0; i < termProb.length; i++) {
        randno -= eventTermProb[index][i];
        if (randno < 0) {
          if (i == 0) i = index;
          else i += 2;
          expression = terminalSet[i][rng.nextInt(terminalSet[i].length)];
          break;
        }
      }
    } else {
      expression = functionSet[paramNum - 1][rng.nextInt(functionSet[paramNum - 1].length)];
      for (int i = 0; i < paramNum; i++) {
        children[i] = new TreeNode(depth + 1);
        children[i].grow(off, val);
      }
    }
  }

  public ArrayList<TreeNode> assembleList(TreeNode node) {
    assembled = new ArrayList<TreeNode>();
    assembled.add(node);
    for (int i = 0; i < paramNum; i++) {
      assembled.addAll(children[i].assembleList(children[i]));
    }
    return assembled;
  }

  public String assembleExpression() {
    String composed = expression;

    for (int i = 0; i < paramNum; i++) {
      composed = "(" + composed.replaceFirst("&"+(i+1), children[i].assembleExpression()) + ")";
    }
    return composed;
  }

  public TreeNode copy() {
    TreeNode copy = new TreeNode(depth);
    copy.paramNum = paramNum;
    copy.children = new TreeNode[paramNum];
    copy.expression = expression;
    for (int i = 0; i < paramNum; i++) {
      copy.children[i] = children[i].copy();
    }
    return copy;
  }

  public TreeNode getSubTree(Boolean isTerminal) {
    if ((treeSize() == 1) && !isTerminal) return getSubTree(true);
    ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();
    if (isTerminal) {
      for (int i = 0; i < assembled.size(); i++) {
        if (assembled.get(i).paramNum == 0) {
          nodes.add(assembled.get(i));
        }
      }
    } else {
      for (int i = 0; i < assembled.size(); i++) {
        if (assembled.get(i).paramNum > 0) {
          nodes.add(assembled.get(i));
        }
      }
    }
    if (nodes.size() == 0) return getSubTree(true);

    TreeNode a = null;
    try {
      a = nodes.get(rng.nextInt(nodes.size()));
    } catch (Exception e) {
      System.out.println("size: " + nodes.size());
    }
    return a;
  }

  public TreeNode getSubTree(Boolean isTerminal, int treeSize) {
    if ((treeSize() == 1) && !isTerminal) return getSubTree(true, treeSize);
    int maxSize = (2 * treeSize) + 1;
    ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();
    if (isTerminal) {
      for (int i = 0; i < assembled.size(); i++) {
        if (assembled.get(i).paramNum == 0) {
          nodes.add(assembled.get(i));
        }
      }
    } else {
      for (int i = 0; i < assembled.size(); i++) {
        if ((assembled.get(i).paramNum > 0) && (assembled.get(i).treeSize() <= maxSize)) {
          nodes.add(assembled.get(i));
        }
      }
    }
    if (nodes.size() == 0) return getSubTree(true);

    TreeNode a = null;
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
    for (int i = 0; i < paramNum; i++) {
      children[i].fixDepths(off);
    }
  }

}
