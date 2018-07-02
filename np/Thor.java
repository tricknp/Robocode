package np;
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

public class Thor extends AdvancedRobot {

	Enemy enemy = new Enemy();
  	Robot robot;

	private final String[] dontAtk = 
	{
		"BorderGuard", 
		"Nocturne", 
		"ProfessorX"
	};

  int direction;
	int muitoProximoParede = 0;
	int limiteParede = 65;
	boolean desvia = false;
	double BF_WIDTH, BF_HEIGHT;

	double tick = 0;

  final double limite = 300; // Limite utilizado para troca de direção

	int shots = 0;
  int a = 0;

	@Override
	public void run() {

		BF_HEIGHT = getBattleFieldHeight();
		BF_WIDTH = getBattleFieldWidth();
		direction = 1;
		robot = new tankWave();

		// Verifica se o inimigo deu um tiro (possivelmente) a partir da analise
		// da energia.
		addCustomEvent(new Condition() {
			public boolean test() {
				if (!enemy.none()) {
					double energyDiff = enemy.getOldEnergy() - enemy.getEnergy();
					return energyDiff >= .1 && energyDiff <= 3;
				}
				return false;
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

			// mantem controle do tempo que passou, e se foi maior que 20 ticks,
			// faz um rapido scan (pra tentar pegar algum inimigo que esteja
			// mais proximo, e corrigir o bug que perde o radar em inimigos
			// Robot (simples))
			if (getTime() - tick > 20) {
				tick = getTime();
				setTurnRadarRight(360);
				scan();
			}

			// inicializa a classe referente ao corpo do robot
			robot.init();
			robot.move();

			// executar threads
			execute();
		} while (true);
	}

	private boolean isAllied(String scannedRobotName) {
	for (String robot : dontAtk) {
	    if (scannedRobotName.toLowerCase().contains(robot.toLowerCase())) {
		return true;
	    }
		}
		return false;
  }

	@Override
	public void onCustomEvent(CustomEvent event) {
		// Caso o evento que verifica se o inimigo atirou é executado, tenta
		// desviar da bala.
		if (getOthers() <= 3 && (event.getCondition().getName().equals("atirou"))) {
			setAhead(49 * (-direction));
		}
		// Caso o evento que verifica a distancia da parede seja executado, para
		// de andar. (no metodo 'move' ele irá verificar a velocidade que o robo
		// esta andando, caso estiver parado, altera a direcao e retoma o
		// movimento.
		if (event.getCondition().getName().equals("parede")) {
			if (muitoProximoParede <= 0) {
				muitoProximoParede += limiteParede;
				setMaxVelocity(0);
			}
		}
	}

	// funcao util para normalizacao do bearing de 180/-180 para 0/360.
	static double normalizeBearing(double angle) {
		while (angle > 180)
			angle -= 360;
		while (angle < -180)
			angle += 360;
		return angle;
	}

	// Se um robo morrer, verifica se é o que estava atacando, caso for, reseta
	// a variavel que estava mantendo suas informacoes, e escaneia por um novo
	// inimigo.
	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		if (enemy.getName().equals(event.getName())) {
			enemy.reset();
			setTurnRadarRight(360);
		}
	}

	// Caso escanear um robo..
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {

		if( isAllied(event.getName() )){
			return;
		}

		// verifica se ele tem energia, se não tiver, enche ele de tiro
		// forca 1.3
		if (event.getEnergy() == 0) {
			setTurnGunRightRadians(Utils
					.normalRelativeAngle(getHeadingRadians() - getGunHeadingRadians() + event.getBearingRadians()));
			if (getTurnRemainingRadians() <= 5)
				fire(1.3);
		}

		// Se nao, verifica se estamos perseguindo alguem ja, se o inimigo
		// escaneado é o mesmo que estamos perseguindo, ou se este novo inimigo
		// esta mais proximo que o nosso atual.
		if (enemy.none() || enemy.getName().equals(event.getName()) || enemy.getDistance() > event.getDistance()) {
			// caso for alguem mais proximo, ou o nosso inimigo atual, ou nao
			// estivermos
			// cacando ninguem, atualiza as informacoes do inimigo.
			enemy.set(event, this);
		}

		// Fixa o radar no inimigo
		setTurnRadarRight(Utils.normalRelativeAngleDegrees(getHeading() - getRadarHeading() + enemy.getBearing()));

		// mantem o canhao mirado onde ele podera estar num instante futuro.
		setTurnGunRightRadians(enemy.getShootMeHere() * 0.85);

		// se o canhao estiver em posicao, atire.
		if (getGunTurnRemainingRadians() <= 1) {
			setFire(enemy.getFirePower());
		}
	}

	// Caso bater em outro robo, mira nele e atira que nem louco.
	@Override
	public void onHitRobot(HitRobotEvent event) {
		setTurnGunRightRadians(getHeadingRadians() - getGunHeadingRadians() + event.getBearingRadians());
		if (getGunTurnRemainingRadians() < 5)
			setFire(3);
	}

	// metodo ativado quando o robô bate na parede
	@Override
	public void onHitWall(HitWallEvent event) {
	}

	// as partes do nosso robo ('rodas')
	class tankWave implements Robot {
		@Override
		public void init() {
			setBodyColor(Color.black);
			setGunColor(Color.red);
			setRadarColor(Color.red);
			setScanColor(Color.red);
			setBulletColor(Color.yellow);
		}
		@Override
		public void move() {
			setTurnRight(10000);
			setMaxVelocity(10);
			ahead(10000);
			setTurnRight(10000);
			back(10000);
			// mantem-se perpendicular a um angulo de 90 graus, com um offset de
			// 16 graus, se aproximando gradativamente do inimigo.
			setTurnRight(normalizeBearing(enemy.getBearing() + 90 - (1 * direction)));
		}
	}
}

//--------------------------------------------------------------------------------------

// Interface para as partes dos robos.
interface Robot {
	public void init();
	public void move();
}

// Classe auxiliar para armazenar as informacoes uteis sobre o robo inimigo.
// Entre outros metodos que facilitam a vida.
final class Enemy {
	private String name;
	private double heading, bearing, velocity, energy, headingRadians, bearingRadians, distance;

	double energyOld;
	double absBearingRadians;
	double displacement;
	private double X, Y;

	double firePower;
	double shootMeHere;

	public Enemy() {
	}

	public Enemy(ScannedRobotEvent e, AdvancedRobot r) {
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

		// posicao absoluta em relacao ao meu robo
		this.absBearingRadians = r.getHeadingRadians() + this.bearingRadians;

		// que forc devo atirar nesse inimigo
		this.firePower = Math.max(r.getBattleFieldHeight(), r.getBattleFieldWidth()) / getDistance();

		// calcula posicao X e Y do inimigo
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
