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
      battlefieldSpec = new BattlefieldSpecification();
  }

  public double[] createBattle(double[] robots) {
    String[] opponents = {"sample.RamFire", "sample.Corners", "sample.Crazy"};

    for (int i = 0; i < robots.length; i++) {
      for (int j = 0; i < 3; i++) {
        RobotSpecification selectedRobots = engine.getLocalRepository(robot+", "+opponents[j]);
        battleSpec = new BattleSpecification(5, battlefield, selectedRobots);
        engine.runBattle(battleSpec, true);
      }
    }


  }

//
// Our private battle listener for handling the battle event we are interested in.
//
class BattleObserver extends BattleAdaptor {

  // Called when the battle is completed successfully with battle results
  public void onBattleCompleted(BattleCompletedEvent e) {
    System.out.println("-- Battle has completed --");

    // Print out the sorted results with the robot names
    System.out.println("Battle results:");
    for (robocode.BattleResults result : e.getSortedResults()) {
      System.out.println("  " + result.getTeamLeaderName() + ": " + result.getScore());
    }
  }

  // Called when the game sends out an information message during the battle
  public void onBattleMessage(BattleMessageEvent e) {
    System.out.println("Msg> " + e.getMessage());
  }

  // Called when the game sends out an error message during the battle
  public void onBattleError(BattleErrorEvent e) {
    System.out.println("Err> " + e.getError());
  }

}
