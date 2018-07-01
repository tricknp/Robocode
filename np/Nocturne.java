package np;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;

public class Nocturne extends AdvancedRobot{ 

	private final String[] dontAtk =
	{
		"BorderGuard",
		"Thor",
		"ProfessorX"
	};

	int movimentar = 1;

	public void run() { // implementando run, classe principal

		setAdjustRadarForRobotTurn(true); // fica com o radar ativo sempre buscando inimigo

		setBodyColor(Color.black);
		setGunColor(Color.red);
		setRadarColor(Color.red);
		setScanColor(Color.red);
		setBulletColor(Color.yellow);

		setAdjustGunForRobotTurn(true); // mantem a arma pronta para quando achar o inimigo

		turnRadarRightRadians(Double.POSITIVE_INFINITY); // fica girando o radar sem parar para a direita

	}

	private boolean isAllied(String scannedRobotName) {
	for (String robot : dontAtk) {
			if (scannedRobotName.toLowerCase().contains(robot.toLowerCase())) {
		return true;
			}
		}
		return false;
	}

	/*
	 * Funcao que escaneia os robos
	 */
	public void onScannedRobot(ScannedRobotEvent e) {

		if( isAllied(e.getName() )){
			return;
		}

		// criando uma variavel double que recebe a distancia do robo escaneado mais a distancia do nosso robo
		double distanciaNRxRI=e.getBearingRadians()+getHeadingRadians();
		// criando uma variavel double que guarda a velocidade do inimigo
		double velocidadeInimigo=e.getVelocity() * Math.sin(e.getHeadingRadians() -distanciaNRxRI);
		// criando uma variavel que recebera a posicao para o disparo
		double ajustaMira;
		setTurnRadarLeftRadians(getRadarTurnRemainingRadians());
		if(Math.random()>.9){
			setMaxVelocity((12*Math.random())+12);
		}
		/*
		 * Abaixo o codigo para medir a distancia do inimigo para o disparo
		 */
		if (e.getDistance() > 150) { // se a distancia do inimigo for menor que 250e maior que 100
			// ajusta a mira com a formula da distancia do nosso robo menos a distancia do rovo inimigo mais a
			// velociadade do robo inimigo, tudo isso dividido por 22
			ajustaMira = robocode.util.Utils.normalRelativeAngle(distanciaNRxRI- getGunHeadingRadians()+velocidadeInimigo/22);
			// ajusta o canhao para o desparo
			setTurnGunRightRadians(ajustaMira);
			setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(distanciaNRxRI-getHeadingRadians()+velocidadeInimigo/getVelocity()));
			// anda de atras do robo inimigo baseado na formula que
			setAhead((e.getDistance() - 140)*movimentar);
			// atira com o nivel 2 de potencia
			setFire(2);
		}
		else{ // esse else e para quando a distancia for menor que 99
			// ajusta a mira com a formula da distancia do nosso robo menos a distancia do rovo inimigo mais a
			// velociadade do robo inimigo, tudo isso dividido por 22
			ajustaMira = robocode.util.Utils.normalRelativeAngle(distanciaNRxRI- getGunHeadingRadians()+velocidadeInimigo/15);
			// ajusta o canhao para o desparo
			setTurnGunRightRadians(ajustaMira);
			setTurnLeft(-90-e.getBearing());
			// anda de atras do robo inimigo baseado na formula que
			setAhead((e.getDistance() - 140)*movimentar);
			// atira com o nivel 3 de potencia
			setFire(3);
		}
	}
	/*
	 * Funcao acionada quando o robo colide com uma parede
	 */
	public void onHitWall(HitWallEvent e){
		movimentar=-movimentar; // a variavel movimentar recebe o valor dela menos 1
	}
	/*
	 * Funcao acionada quando o robo se colide com outro
	 */
	public void onHitRobot(HitRobotEvent e){
		setAhead(movimentar*55); // se movimenta para, se afastar do inimigo
	}
	/*
	 * Quando o nosso robo e acertado
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		//setAhead(movimentar*55+10);  // se movimenta para, se afastar do inimigo
		fire(3);
	}


}
