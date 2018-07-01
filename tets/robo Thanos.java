package Trabalho;
import robocode.*;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Thanos - a robot by (your name here)
 */
public class Thanos extends Robot
{
	/**
	 * run: Thanos's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		
		setBodyColor(Color.blue);
		setGunColor(Color.white);
		setRadarColor(Color.red);
		setScanColor(Color.black);
		setBulletColor(Color.black);

		// Robot main loop
		while(true) {
			// Replace the next 4 lines with any behavior you would like
			setTurnRight(10000);
			setTurnRight(10000);

			ahead(10000);
			turnGunRight(10000);
			back(1000);
			
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		fire(10);
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		if (e.getBearing() > -10 && e.getBearing() < 10) {
			fire(5);
		}
		if (e.isMyFault()) {
			turnRight(25);
	}
   }
 }
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}	
}
