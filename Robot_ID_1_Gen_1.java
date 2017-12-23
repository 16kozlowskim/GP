package u1624396;
import robocode.*;
public class Robot_ID_1_Gen_1 extends Robot {
  public void run() {
    while(true) {
      turnLeft(Double.POSITIVE_INFINITY);
      turnRadarLeft(Double.POSITIVE_INFINITY);
      turnGunLeft(Double.POSITIVE_INFINITY);
    }
  }
  public void onScannedRobot(ScannedRobotEvent e) {
    ahead(((e.getHeading()) > 0 ? (getGunHeading()) : (e.getHeading())));
    turnRadarLeft(((getOthers()) > (getHeading()) ? (getEnergy()) : (e.getVelocity())));
    turnLeft(((e.getVelocity()) / (e.getHeading())));
    turnGunLeft(((getEnergy()) > (e.getDistance()) ? (getBattleFieldWidth()) : (e.getEnergy())));
    fire(((e.getDistance()) * (e.getDistance())));
  }
}