package de.tum.in.ase.eist.car;

import de.tum.in.ase.eist.Dimension2D;
import de.tum.in.ase.eist.gameview.GameBoardUI;

public class BallCar extends Car {
    private static final String BALL_CAR_IMAGE_FILE = "LightOrb.gif";

    private int radialPos = 0;
    private static final int RADIUS = GameBoardUI.EFFECTIVE_RADIUS;
    public static final Double RADIAL_BALL_WIDTH = 15.0;
    public static final int SIZE = 38;

    public BallCar(Dimension2D gameBoardSize) {
        super(gameBoardSize);
        setSize(new Dimension2D(SIZE, SIZE));
        setIconLocation(BALL_CAR_IMAGE_FILE);
    }

    public int getRadialPos() {
        return radialPos;
    }

    public void setRadialPos(int radialPos) {
        this.radialPos = radialPos;
    }

    public int getRadius() {
        return RADIUS;
    }
}
