package Trabalho;
import robocode.*;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * TortaoPraEsquerda - a robot by (your name here)
 */
public class TortaoPraEsquerda extends AdvancedRobot
{
	/**
	 * run: TortaoPraEsquerda's default behavior
	 */
	public void run() {
ahead(1000);
		while(true) {
		    ahead(200);
	  		turnRight(120);
			ahead(150);
			turnLeft(120);
			
			
			
			
			
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		fire(2);
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
	  	
       	setTurnRight(240);
		execute();
			
	  		
	  	
	}	
}
