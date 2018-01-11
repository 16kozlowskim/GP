import robocode.control.*;
import robocode.control.events.*;
import robocode.BattleResults;
import java.io.*;

public class BattleRunner {

  RobocodeEngine engine;
  BattleObserver battleObserver;
  BattlefieldSpecification battlefield;
  BattleSpecification battleSpec;

  public static void main(String[] args) {

    String[] robots = new String[args.length - 2];
    for (int i = 0; i < robots.length; i++) {
      robots[i] = args[i + 2];
    }

    BattleRunner battle = new BattleRunner(args[0]);

    double[] results = battle.fight(robots, Integer.parseInt(args[1]));

    for (int i = 0; i < results.length; i++) {
      System.out.println(results[i] + ",");
    }

  }

  public BattleRunner(String pathToRobocode) {
      engine = new RobocodeEngine(new java.io.File(pathToRobocode));
      //engine.setLogMessagesEnabled(false);
      battleObserver = new BattleObserver();
      engine.addBattleListener(battleObserver);
      engine.setVisible(false);
      battlefield = new BattlefieldSpecification();
  }

  public double[] fight(String[] robots, int generation) {

    String[] opponents = {
      "sample.Crazy",
      "sample.SpinBot",
      "sample.Walls",
      "sample.TrackFire",
      "sample.Tracker",
      "sample.Corners"
    };

    //if (generation > 0) {
      //opponents[0] = "supersample.SuperCrazy*";
      //opponents[1] = "supersample.SuperMercutio 1.0";
      //opponents[2] = "jk.mega.DrussGT 3.1.4159";
    //}

    BattleResults[] results;
    double[] fitness = new double[robots.length * 2];

    for (int i = 0; i < robots.length; i++) {

      //System.out.println(robots[i] + " is testing its mettle.");

      double robotScore = 0;
      double opponentScore = 0;
      double robotScoreFFA = 0;
      double opponentScoreFFA = 0;

      for (int j = 0; j < opponents.length; j++) {
        RobotSpecification[] selectedRobots = engine.getLocalRepository(opponents[j] + ", " + robots[i]);
        battleSpec = new BattleSpecification(5, battlefield, selectedRobots);
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
      RobotSpecification[] selectedRobots = engine.getLocalRepository(robots[i] + ", " + opponents[0] + ", " + opponents[1] + ", " + opponents[2]);
      battleSpec = new BattleSpecification(5 * opponents.length, battlefield, selectedRobots);
      engine.runBattle(battleSpec, true);

      results = battleObserver.getResult();

      for (int j = 0; j < selectedRobots.length; j++) {
        if (results[j].getTeamLeaderName().equals(robots[i])) {
          robotScoreFFA += results[j].getScore();
        } else {
          opponentScoreFFA += results[j].getScore();
        }
      }

      fitness[i] = (robotScore / (robotScore + opponentScore)) + 0.01;
      fitness[i + robots.length] = (robotScoreFFA / (robotScoreFFA + opponentScoreFFA)) + 0.01;
    }
    return fitness;
  }
}

class BattleObserver extends BattleAdaptor {

  private robocode.BattleResults[] result;

  public BattleResults[] getResult() {
    return result;
  }

  public void onBattleCompleted(BattleCompletedEvent e) {
    result = e.getIndexedResults();
  }

}
