package u1624396;
import robocode.*;
public class Robot_ID_2_Gen_1 extends Robot {
  public void run() {
    while(true) {
      turnLeft(Double.POSITIVE_INFINITY);
      turnRadarLeft(Double.POSITIVE_INFINITY);
      turnGunLeft(Double.POSITIVE_INFINITY);
    }
  }
  public void onScannedRobot(ScannedRobotEvent e) {
    ahead(((e.getBearing()) > (e.getBearing()) ? (e.getVelocity()) : (getBattleFieldWidth())));
    turnRadarLeft(((e.getEnergy()) * (getHeading())));
    turnLeft(((e.getHeading()) > (e.getDistance()) ? (getRadarHeading()) : (getBattleFieldWidth())));
    turnGunLeft(((getX()) / (e.getDistance())));
    fire((Math.toDegrees((getGunCoolingRate()))));
  }
}