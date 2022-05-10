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
	private static final int ANGLE_180_DEG = 180;
	private static final int ANGLE_360_DEG = 360;

	// lol!
	private final Car userCar;
	private final GameBoardUI gameBoardUI;

	/**
	 * Creates a MouseSteering instance for a specific GameBoardUI and a car that
	 * the user needs to steer with their mouse.
	 *
	 * @param userCar     the car that should be steered by the user
	 * @param gameBoardUI
	 */
	public MouseSteering(Car userCar, GameBoardUI gameBoardUI) {
		this.userCar = userCar;
		this.gameBoardUI = gameBoardUI;
	}

	public void mouseMoved(MouseEvent moveEvent) {
		Double mpX = GameBoardUI.MIDPOINT.getX();
		Double mpY = GameBoardUI.MIDPOINT.getY();

		int fxRad = GameBoardUI.EFFECTIVE_RADIUS;

		Point2D mousePos = new Point2D(moveEvent.getX() - mpX, moveEvent.getY() - mpY);
		// a hint of linear algebra
		int hyp = (int) Math.sqrt(mousePos.getX() * mousePos.getX() + mousePos.getY() * mousePos.getY());

		userCar.setPosition(fxRad * mousePos.getX() / hyp + mpX, fxRad * mousePos.getY() / hyp + mpY);

		if (userCar instanceof BallCar bc) {
			Double posX = userCar.getPosition().getX();
			Double posY = userCar.getPosition().getY();

			int radialPos = (int) (Math.atan2(-(posY - mpY), posX - mpX) * ANGLE_180_DEG / Math.PI);
			if (radialPos < 0) {
				radialPos = ANGLE_360_DEG + radialPos;
			}
			bc.setRadialPos(radialPos);
			if (!gameBoardUI.getGameBoard().isRunning()) {
				gameBoardUI.paint();
			}
		}
	}

//	public void mousePressed(MouseEvent clickEvent) {
//		Point2D carPosition = userCar.getPosition();
//		Point2D clickPosition = new Point2D(clickEvent.getX(), clickEvent.getY());
//		double deltaX = clickPosition.getX() - carPosition.getX();
//		deltaX = Math.abs(deltaX);
//		double deltaY = clickPosition.getY() - carPosition.getY();
//		int degree = (int) Math.toDegrees(Math.atan2(deltaY, deltaX));
//
//		if (clickPosition.getX() > carPosition.getX()) {
//			degree = ANGLE_90_DEGREES - degree;
//		} else {
//			degree = ANGLE_270_DEGREES + degree;
//		}
//
//		userCar.setDirection(degree);
//	}
}
