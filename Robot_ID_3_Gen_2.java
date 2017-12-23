package u1624396;
import robocode.*;
public class Robot_ID_3_Gen_2 extends Robot {
  public void run() {
    while(true) {
      turnLeft(Double.POSITIVE_INFINITY);
      turnRadarLeft(Double.POSITIVE_INFINITY);
      turnGunLeft(Double.POSITIVE_INFINITY);
    }
  }
  public void onScannedRobot(ScannedRobotEvent e) {
    ahead(((e.getBearing()) * (e.getEnergy())));
    turnRadarLeft(((getVelocity()) > 0 ? (getBattleFieldHeight()) : (getVelocity())));
    turnLeft(((getGunHeading()) / (e.getHeading())));
    turnGunLeft(((getRadarHeading()) * (e.getEnergy())));
    fire(((e.getBearing()) + (getVelocity())));
  }
}