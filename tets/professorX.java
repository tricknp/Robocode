package nervousers;
import robocode.*;
import java.awt.*;
import java.awt.geom.*;

public class ProfessorX extends AdvancedRobot
{
	boolean movingforward;

    Point2D direcao;  // Vetor normalizado com a direção de movimento do tanque
    Point2D canto;    // Vetor do tanque até o canto mais próximo a que ele se aproxima
    double distancia; // Distância até as paredes

    final double limite = 200; // Limite utilizado para troca de direção
	
	public void run() {
	ahead(1000);
		while(true) {
		    ahead(200);
	  	    turnRight(120);
	            ahead(150);
	            turnLeft(120);
			
		    if(distancia <= limite)
                      turnLeft(180);

		}
	}

	

	public void calculaDistancia() {

        // Pega a altura e largura do campo de batalha e posição x,y do tanque
        double h = getBattleFieldHeight(); // Altura
        double w = getBattleFieldWidth();  // Largura
        double x = getX();
        double y = getY();

        // Pega a direção em que o tank se move e a sua posição atual (x, y) no campo de batalha
        double ang = getHeading(); // O ângulo está em graus, variando entre 0 (apontando pra cima) e 359) no sentido horário

        // Calcula os vetor normal de direção do tanque
        double dx = Math.sin(Math.toRadians(ang));
        double dy = Math.cos(Math.toRadians(ang));
        direcao = new Point2D.Double(dx, dy);

        // Calcula o vetor do tanque em direção ao canto mais próximo da direção e sentido que ele segue
        dx = (direcao.getX() > 0) ? w - x : -x;
        dy = (direcao.getY() > 0) ? h - y : -y;
        canto = new Point2D.Double(dx, dy);

        // Calcula os angulos entre o vetor de direcao e os vetores dos os eixos x e y
        double angX = Math.acos(Math.abs(direcao.getX()));
        double angY = Math.acos(Math.abs(direcao.getY()));

        // A distância é o cateto adjascente do menor ângulo
        if(angY < angX)
            distancia = Math.abs(canto.getY() / Math.cos(angY));
        else
            distancia = Math.abs(canto.getX() / Math.cos(angX));
    }   

    public void onPaint(Graphics2D g) {
        // Desenha a linha até a parede em amarelo se maior do que o limite, e em vermelho se menor do que o limite
        if(distancia <= limite)
            g.setColor(java.awt.Color.RED);
        else
            g.setColor(java.awt.Color.YELLOW);
        g.drawLine((int) getX(), (int) getY(), (int) (getX() + (distancia * direcao.getX())), (int) (getY() + (distancia * direcao.getY())));

        // Desenha o valor da distância em branco
        g.setColor(java.awt.Color.WHITE);
        g.drawString("Distancia: " + distancia, 10, 10);

        // Desenha as componentes do vetor do canto tracejados em branco
        Stroke pontilhado = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0);
        g.setStroke(pontilhado);
        g.setColor(java.awt.Color.WHITE);
        g.drawLine((int) getX(), (int) getY(), (int) (getX() + canto.getX()), (int) getY()); // Componente em X
        g.drawLine((int) getX(), (int) getY(), (int) getX(), (int) (getY() + canto.getY())); // Componente em Y
     }

    public void onScannedRobot(ScannedRobotEvent e) {  
        //turnGunRight(getHeading() - getGunHeading() + e.getBearing());  
        //fire(1);
    }  

    public void onHitByBullet(HitByBulletEvent e) {  

    }  

    public void onHitWall(HitWallEvent e) {  

    }   
}
