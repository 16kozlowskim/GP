import robocode.control.*;
import robocode.control.events.*;
import robocode.BattleResults;

//
// Application that demonstrates how to run two sample robots in Robocode using the
// RobocodeEngine from the robocode.control package.
//
// @author Flemming N. Larsen
//

public class BattleRunner {

  RobocodeEngine engine;
  BattleObserver battleObserver;
  BattlefieldSpecification battlefield;
  BattleSpecification battleSpec;

  public BattleRunner() {
      engine = new RobocodeEngine(new java.io.File("C:/Robocode"));
      RobocodeEngine.setLogMessagesEnabled(false);
      battleObserver = new BattleObserver();
      engine.addBattleListener(battleObserver);
      engine.setVisible(false);
      battlefield = new BattlefieldSpecification();
  }

  public double[] createBattle(String[] robots) {
    String[] opponents = {"supersample.SuperCrazy*"};
    BattleResults[] results;
    double[] fitness = new double[robots.length];

    for (int i = 0; i < robots.length; i++) {
      double robotScore = 0;
      double opponentScore = 0;
      for (int j = 0; j < opponents.length; j++) {
        RobotSpecification[] selectedRobots = engine.getLocalRepository(opponents[j] + ", " + robots[i]);
        battleSpec = new BattleSpecification(10, battlefield, selectedRobots);
        engine.runBattle(battleSpec, true);

        results = battleObserver.getResult();

        if (results[0].getTeamLeaderName().equals(robots[i])) {
          robotScore += results[0].getScore();
          opponentScore += results[1].getScore();
        } else {
          robotScore += results[1].getScore();
          opponentScore += results[0].getScore();
        }
      }
      fitness[i] = (robotScore / (robotScore + opponentScore)) + 0.01;
    }
    return fitness;


  }
}

class BattleObserver extends BattleAdaptor {

  robocode.BattleResults[] result;

  public BattleResults[] getResult() {
    return result;
  }

  public void onBattleCompleted(BattleCompletedEvent e) {
    result = e.getIndexedResults();
  }

  // Called when the game sends out an information message during the battle
  public void onBattleMessage(BattleMessageEvent e) {
    //System.out.println("Msg> " + e.getMessage());
  }

  // Called when the game sends out an error message during the battle
  public void onBattleError(BattleErrorEvent e) {
    System.out.println("Err> " + e.getError());
  }
}
