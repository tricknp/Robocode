package nervousers;
import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.Condition;
import robocode.CustomEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
//inspired farrapos

public class Nervouser extends AdvancedRobot {

	CageInimigo enemy = new CageInimigo();
	int direction;
	int muitoProximoParede = 0;
	int limiteParede = 65;
	boolean desvia = false;
	double BF_WIDTH, BF_HEIGHT;
	RobotPart wheels;

	double tick = 0;

	@Override
	public void run() {
		
		BF_HEIGHT = getBattleFieldHeight();
		BF_WIDTH = getBattleFieldWidth();
		direction = 1;
		wheels = new tankWave();

		// Verifica se o inimigo deu um tiro (possivelmente) a partir da analise
		// da energia.
		addCustomEvent(new Condition("Canh�oza�o na poupa") {
			public boolean test() {
				if (!enemy.none()) {
					double energyDiff = enemy.getOldEnergy() - enemy.getEnergy();
					return energyDiff >= .1 && energyDiff <= 3;
				}
				return false;
			}
		});

		// Verifica se est� muito proximo da parede usando como referencia a
		// margem em pixels definida na variavel 'limiteParede'.
		addCustomEvent(new Condition("parede") {
			public boolean test() {
				return (getX() <= limiteParede ||
						// muito proximo a parede direita
						getX() >= BF_WIDTH - limiteParede ||
						// muito proximo ao limite inferior
						getY() <= limiteParede ||
						// muito proximo ao limite superior
						getY() >= BF_HEIGHT - limiteParede);
			}
		});

		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);

		// limpa o nome de qualquer inimigo armazenado, para iniciar o round.
		enemy.reset();

		// faz um primeiro scan para pegar um inimigo.
		setTurnRadarRight(360);

		do {

			// mant�m controle do tempo que passou, e se foi maior que 20 ticks,
			// faz um rapido scan (pra tentar pegar algum inimigo que esteja
			// mais proximo, e corrigir o bug que perde o radar em inimigos
			// Robot (simples))
			if (getTime() - tick > 20) {
				tick = getTime();
				setTurnRadarRight(360);
				scan();
			}

			// inicializa a classe referente ao corpo do robot
			wheels.init();
			wheels.move();

			// executar threads
			execute();
		} while (true);

	}

	@Override
	public void onCustomEvent(CustomEvent event) {
		// Caso o evento que verifica se o inimigo atirou � executado, tenta
		// desviar da bala.
		if (getOthers() <= 3 && (event.getCondition().getName().equals("atirou"))) {
			setAhead(49 * (-direction));
			System.out.println(enemy.getName() + " atirou poha O_O ");
		}
		// Caso o evento que verifica a distancia da parede seja executado, para
		// de andar. (no metodo 'move' ele ir� verificar a velocidade que o robo
		// est� andando, caso estiver parado, altera a dire��o e retoma o
		// movimento.
		if (event.getCondition().getName().equals("parede")) {
			if (muitoProximoParede <= 0) {
				muitoProximoParede += limiteParede;
				setMaxVelocity(0);
			}
		}
	}

	// fun��o util para normaliza��o do bearing de 180/-180 para 0/360.
	static double normalizeBearing(double angle) {
		while (angle > 180)
			angle -= 360;
		while (angle < -180)
			angle += 360;
		return angle;
	}

	// Se um robo morrer, verifica se � o que estava atacando, caso for, reseta
	// a variavel que estava mantendo suas informa��es, e escaneia por um novo
	// inimigo.
	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		if (enemy.getName().equals(event.getName())) {
			System.out.println("MORREU BAITOLA, BIRLLL");
			enemy.reset();
			setTurnRadarRight(360);
		}
	}

	// Caso escanear um robo..
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		// verifica se ele tem energia, se n�o tiver, enche a bunda dele de tiro
		// for�a 1.3
		if (event.getEnergy() == 0) {
			setTurnGunRightRadians(Utils
					.normalRelativeAngle(getHeadingRadians() - getGunHeadingRadians() + event.getBearingRadians()));
			if (getTurnRemainingRadians() <= 5)
				fire(1.3);
		}

		// Se n�o, verifica se estamos perseguindo alguem j�, se o inimigo
		// escaneado � o mesmo que estamos perseguindo, ou se este novo inimigo
		// est� mais proximo que o nosso atual.
		if (enemy.none() || enemy.getName().equals(event.getName()) || enemy.getDistance() > event.getDistance()) {
			// caso for alguem mais proximo, ou o nosso inimigo atual, ou n�o
			// estivermos
			// ca�ando ninguem, atualiza as informa��es do inimigo.
			enemy.set(event, this);
		}

		// Fixa o radar no inimigo
		setTurnRadarRight(Utils.normalRelativeAngleDegrees(getHeading() - getRadarHeading() + enemy.getBearing()));

		// mant�m o canh�o mirado onde ele poder� estar num instante futuro.
		setTurnGunRightRadians(enemy.getShootMeHere() * 0.85);

		// se o canh�o estiver em posi��o, atire.
		if (getGunTurnRemainingRadians() <= 1) {
			setFire(enemy.getFirePower());
		}
	}

	// Caso bater em outro robo, mira nele e atira pra crl.
	@Override
	public void onHitRobot(HitRobotEvent event) {
		System.out.println("AFE MANO " + event.getName() + " VAI BATE CRL .-.");
		setTurnGunRightRadians(getHeadingRadians() - getGunHeadingRadians() + event.getBearingRadians());
		if (getGunTurnRemainingRadians() < 5)
			setFire(3);
	}

	// Caso bater em uma parede.. malditas paredes..
	@Override
	public void onHitWall(HitWallEvent event) {
		System.out.println("opa sopa");
	}

	// as partes do nosso robo ('rodas')
	class tankWave implements RobotPart {
		@Override
		public void init() {
			setBodyColor(Color.BLACK); // Chocolate Brown
			setGunColor(Color.BLACK); // Aqua Marine
			setRadarColor(Color.BLACK); // Orange Chocolate
			setBulletColor(Color.BLACK); // Burly wood
			setScanColor(Color.BLACK); // Olive Green}
		}
		@Override
		public void move() {
			// mantem-se perpendicular a um angulo de 90 graus, com um offset de
			// 16 graus, se aproximando gradativamente do inimigo.
			setTurnRight(normalizeBearing(enemy.getBearing() + 90 - (1 * direction)));

			// mant�m controle de qu�o distantes estamos da parede 
			if (muitoProximoParede > 0) {
				muitoProximoParede--;
			}

			// caso estivermos parados, ou seja, batemos em algo. Volta a andar
			// na dire��o contr�ria.
			if (getVelocity() == 0) {
				setMaxVelocity(8);
				direction *= -1;
				setAhead(1000 * direction);
			}

		}

	}
}

// INNER CLASSES

// Interface para as partes dos robos.
interface RobotPart {
	public void init();

	public void move();
}

// Classe auxiliar para armazenar as informa��es uteis sobre o robo inimigo.
// Entre outros m�todos que facilitam a vida.
final class CageInimigo {
	private String name;
	private double heading, bearing, velocity, energy, headingRadians, bearingRadians, distance;

	double energyOld;
	double absBearingRadians;
	double displacement;
	private double X, Y;

	double firePower;
	double shootMeHere;

	public CageInimigo() {
	}

	public CageInimigo(ScannedRobotEvent e, AdvancedRobot r) {
		set(e, r);
	}

	public void reset() {
		this.name = "";
	}

	public boolean none() {
		return this.getName().isEmpty();
	}

	// Mantem controle da energia atual e anterior do robo. (para ver se ele ta
	// atirando em mim)
	private void setEnergy(double energyNew) {
		this.energyOld = this.energy;
		this.energy = energyNew;
	}

	public void set(ScannedRobotEvent e, AdvancedRobot r) {
		this.name = e.getName();
		this.heading = e.getHeading();
		this.headingRadians = e.getHeadingRadians();
		this.bearing = e.getBearing();
		this.bearingRadians = e.getBearingRadians();
		this.velocity = e.getVelocity();
		setEnergy(e.getEnergy());
		this.distance = e.getDistance();

		// posi��o absoluta em rela��o ao meu robo
		this.absBearingRadians = r.getHeadingRadians() + this.bearingRadians;

		// que for�a devo atirar nesse inimigo
		this.firePower = Math.max(r.getBattleFieldHeight(), r.getBattleFieldWidth()) / getDistance();

		// calcula posi��o X e Y do inimigo
		this.X = r.getX() + Math.sin(getAbsBearingRadians()) * getDistance();
		this.Y = r.getY() + Math.cos(getAbsBearingRadians()) * getDistance();

		// calcula deslocamento
		this.displacement = getVelocity() * Math.sin(getHeadingRadians() - getAbsBearingRadians())
				/ Rules.getBulletSpeed(getFirePower());

		// onde devo atirar
		this.shootMeHere = Utils
				.normalRelativeAngle(getAbsBearingRadians() - r.getGunHeadingRadians() + getDisplacement());
	}

	public double getShootMeHere() {
		return shootMeHere;
	}

	public double getFirePower() {
		return firePower;
	}

	public double getAbsBearingRadians() {
		return absBearingRadians;
	}

	public double getDisplacement() {
		return displacement;
	}

	public double getX() {
		return X;
	}

	public double getY() {
		return Y;
	}

	public String getName() {
		return name;
	}

	public double getHeading() {
		return heading;
	}

	public double getBearing() {
		return bearing;
	}

	public double getVelocity() {
		return velocity;
	}

	public double getOldEnergy() {
		return energyOld;
	}

	public double getEnergy() {
		return energy;
	}

	public double getHeadingRadians() {
		return headingRadians;
	}

	public double getBearingRadians() {
		return bearingRadians;
	}

	public double getDistance() {
		return distance;
	}
}
