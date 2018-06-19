package robots;


import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

import java.awt.*;


public class GiraGira extends AdvancedRobot {

	int shots = 0;
    int a = 0;	

	/**
	 * SpinBot's run method - Circle
	 */
	public void run() {
		// Set colors
		setBodyColor(Color.red);
		setGunColor(Color.black);
		setRadarColor(Color.red);
		setScanColor(Color.red);
		setBulletColor(Color.red);
		
	//loop infinito
	 while (true) {
			/*  
				Aqui vai a rotina de atividades que o robô irá fazer
				
				setTurnRight(1000) -> Move 1000 pixels a direita
				setMaxVelocity(10) -> seta o limite da nossa velocidade
				ahead(10000)       -> Anda 10000 pixels a frente
				back(10000)        -> Anda 10000 pixels para tras
				
	** Como está em um loop infinito, quando acabar a rotina, ele irá fazer ela novamente **
			*/
			setTurnRight(10000);
			setMaxVelocity(10);
			
			// Começa a andar e a girar
			ahead(10000);
			setTurnRight(10000);
			back(10000);	
			// Repeat.
		}
	}

	/**
	 * onScannedRobot: mete bala nos robozinhos tudo
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		fire(10);
	}

	/**
	 * onHitRobot:  Se é nossa falta, vamos parar de girar e mover,
       então precisamos virar novamente para continuar girando.
	 */
	public void onHitRobot(HitRobotEvent e) {
		if (e.getBearing() > -10 && e.getBearing() < 10) {
			fire(5);
		}
		if (e.isMyFault()) {
			turnRight(25);
		}
	}
}
