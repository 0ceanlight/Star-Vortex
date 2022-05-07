package de.tum.in.ase.eist.car;

import de.tum.in.ase.eist.Dimension2D;
import de.tum.in.ase.eist.gameview.GameBoardUI;

public class BallCar extends Car {
    private static final String BALL_CAR_IMAGE_FILE = "LightOrb.gif";

    private static final int MIN_SPEED_FAST_CAR = 2;
    private static final int MAX_SPEED_FAST_CAR = 10;

    private int radialPos = 0;
    private final int radius = GameBoardUI.EFFECTIVE_RADIUS;
    public static final Double RADIAL_BALL_WIDTH = 15.0;

    public BallCar(Dimension2D gameBoardSize) {
        super(gameBoardSize);
        setSize(new Dimension2D(30, 30));
//        setMinSpeed(MIN_SPEED_FAST_CAR);
//        setMaxSpeed(MAX_SPEED_FAST_CAR);
//        setRandomSpeed();
        setIconLocation(BALL_CAR_IMAGE_FILE);
    }

    public int getRadialPos() {
        return radialPos;
    }

    public void setRadialPos(int radialPos) {
        this.radialPos = radialPos;
    }

    public int getRadius() {
        return radius;
    }
}
