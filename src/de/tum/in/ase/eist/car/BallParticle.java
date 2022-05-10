package de.tum.in.ase.eist.car;

import de.tum.in.ase.eist.Dimension2D;
import de.tum.in.ase.eist.Point2D;

public class BallParticle extends Car {

    // TODO Add Sparkle.png to resources
    private static final String BALL_PARTICLE_IMAGE_FILE = "sparkle.png";
    private Point2D position;
    private Point2D midPoint;
    private static final Double SPEED = 1.05;
    public static final int SIZE = 13;

    public boolean isOnBoard() {
        return isOnBoard;
    }

    private boolean isOnBoard = true;
    /**
     * Constructor, taking the maximum coordinates of the game board. Each car gets
     * a random X and Y coordinate, a random direction and a random speed.
     * <p>
     * The position of the car cannot be larger then the dimensions of the game
     * board.
     *
     * @param gameBoardSize dimensions of the game board
     */
    public BallParticle(Dimension2D gameBoardSize, Point2D position, Point2D midPoint) {
        super(gameBoardSize);
        this.setSize(new Dimension2D(SIZE, SIZE));
        this.position = position;
        this.midPoint = midPoint;
        setIconLocation(BALL_PARTICLE_IMAGE_FILE);
    }

    @Override
    public void drive(Dimension2D gameBoardSize) {
        Double x = (position.getX() - midPoint.getX()) * SPEED + midPoint.getX();
        Double y = (position.getY() - midPoint.getY()) * SPEED + midPoint.getY();

//        if ((x < 0 || x > this.getSize().getWidth()) || (y < 0 || y > this.getSize().getHeight())) {
        if (x < 0 || x > gameBoardSize.getWidth() || y < 0 || y > gameBoardSize.getHeight()) {
            this.isOnBoard = false;
        }

        this.position = new Point2D(x, y);
    }

    @Override
    public Point2D getPosition() {
        return position;
    }
}
