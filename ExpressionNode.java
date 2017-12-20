
public class ExpressionNode {

  private ExpressionNode[] children;
  private String expression;

  private static final double
    eventTermProb = 0.5,
    constTermProb = 0.05,
    genTermProb = 0.45,
    termProb[] = {
      eventTermProb,
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
    };

  private int depth, arity;
  private int maxDepth = 6;
  private int minDepth = 3;

  public ExpressionNode(int depth, int arity) {
    this.depth = depth;
    this.arity = arity;
  }

  public String compose() {
    String composed = expression;

    for (int i = 0; i < this.arity; i++) {
      composed = composed.replaceFirst("&"+i, children[i].compose());
    }

    composed = "("+composed+")";

    return composed;
  }

  public void grow(int depth, int arity) {

  }

  private static final String[] onScannedRobotTerminals = {
    "e.getBearing()", // in double degrees (-180 <= getBearing() < 180)
    "e.getDistance()", // in double pixels
    "e.getEnergy()", // in double
    "e.getHeading()", // in double degrees (0 <= getHeading() < 360)
    "e.getVelocity()" // in double pixels/turn
  };

  private static final String[] constantTerminals = {
    "Math.PI",
    "Math.random()",
    "0"
  };

  private static final String[] generalTerminals = {
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

  private static final String[][] terminalSet = {
    onScannedRobotTerminals,
    constantTerminals,
    generalTerminals
  };

  private static final String[] unaryFunctions = {
    "Math.sin(&1)",
    "Math.cos(&1)",
    "Math.asin(&1)",
    "Math.acos(&1)",
    "Math.abs(&1)",
    "-1 * &1"
  };

  private static final String[] binaryFunctions = {
    "Math.min(&1, &2)",
    "Math.max(&1, &2)",
    "&1 + &2",
    "&1 - &2",
    "&1 * &2",
    "&1 / &2"
  };

  private static final String[] ternaryFunctions = {
    "&1 > 0 ? &2 : &3"
  };

  private static final String[] quaternaryFunctions = {
    "&1 > &2 ? &3 : &4"
  };

  private static final String[][][] functionTerminalSet = {
    terminalSet,
    unaryFunctions,
    binaryFunctions,
    ternaryFunctions,
    quaternaryFunctions
  };

}
