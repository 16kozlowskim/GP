package u1624396;
import robocode.*;

public class MyRobot extends Robot {

	public void run() {
		while(true) {
			turnLeft(); // double degrees
			turnRadarLeft(); // double degrees
			turnGunLeft(); // double degrees
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		fire(); // 0.1 <= double power <= 3
		ahead(); // double distance
		turnLeft(); // double degrees
		turnRadarLeft(); // double degrees
		turnGunLeft(); // double degrees
	}

}
