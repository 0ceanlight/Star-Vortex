package de.tum.in.ase.eist.usercontrol;

import de.tum.in.ase.eist.Point2D;
import de.tum.in.ase.eist.car.BallCar;
import de.tum.in.ase.eist.car.Car;
import de.tum.in.ase.eist.gameview.GameBoardUI;
import javafx.scene.input.MouseEvent;

/**
 * This class is responsible for the handling the MOUSE_PRESSED Event, i.e. the
 * steering of the user's car.
 */
public class MouseSteering {

	// lol!
	private static final int ANGLE_90_DEGREES = 90;
	private static final int ANGLE_270_DEGREES = 270;

	private final Car userCar;

	/**
	 * Creates a MouseSteering instance for a specific GameBoardUI and a car that
	 * the user needs to steer with their mouse.
	 *
	 * @param userCar     the car that should be steered by the user
	 */
	public MouseSteering(Car userCar) {
		this.userCar = userCar;
	}

	public void mouseMoved(MouseEvent moveEvent) {
		Point2D mousePos = new Point2D(moveEvent.getX() - GameBoardUI.MIDPOINT.getX(), moveEvent.getY() - GameBoardUI.MIDPOINT.getY());
		// a hint of linear algebra
		int hyp = (int) Math.sqrt(mousePos.getX() * mousePos.getX() + mousePos.getY() * mousePos.getY());

		userCar.setPosition(GameBoardUI.EFFECTIVE_RADIUS * mousePos.getX() / hyp + GameBoardUI.MIDPOINT.getX(), GameBoardUI.EFFECTIVE_RADIUS * mousePos.getY() / hyp + GameBoardUI.MIDPOINT.getY());

		if (userCar instanceof BallCar bc) {
//			System.out.println(((int) (userCar.getPosition().getX() - GameBoardUI.MIDPOINT.getX())) + ", " + ((int) userCar.getPosition().getY()- GameBoardUI.MIDPOINT.getY()));
			int radialPos = (int) (Math.atan2(-(userCar.getPosition().getY() - GameBoardUI.MIDPOINT.getY()), userCar.getPosition().getX() - GameBoardUI.MIDPOINT.getX()) * 180 / Math.PI);
			if (radialPos < 0) {
				radialPos = 360 + radialPos;
			}
			bc.setRadialPos(radialPos);
//			System.out.println(radialPos);
		}
	}

	public void mousePressed(MouseEvent clickEvent) {
		Point2D carPosition = userCar.getPosition();
		Point2D clickPosition = new Point2D(clickEvent.getX(), clickEvent.getY());
		double deltaX = clickPosition.getX() - carPosition.getX();
		deltaX = Math.abs(deltaX);
		double deltaY = clickPosition.getY() - carPosition.getY();
		int degree = (int) Math.toDegrees(Math.atan2(deltaY, deltaX));

		if (clickPosition.getX() > carPosition.getX()) {
			degree = ANGLE_90_DEGREES - degree;
		} else {
			degree = ANGLE_270_DEGREES + degree;
		}

		userCar.setDirection(degree);
	}
}
