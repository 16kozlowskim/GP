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
    ahead(((e.getDistance()) / (e.getHeading())));
    turnRadarLeft((Math.min((e.getEnergy()), (e.getHeading()))));
    turnLeft((Math.cos((getBattleFieldWidth()))));
    turnGunLeft(((e.getBearing()) * (getY())));
    fire(((e.getVelocity()) > (e.getEnergy()) ? (e.getVelocity()) : (getVelocity())));
  }
}