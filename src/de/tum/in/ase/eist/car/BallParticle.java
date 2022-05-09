package de.tum.in.ase.eist.car;

import de.tum.in.ase.eist.Dimension2D;
import de.tum.in.ase.eist.Point2D;
import de.tum.in.ase.eist.gameview.GameBoardUI;

public class BallParticle extends Car {
    private Point2D position;
    private Double speed = 1.06;

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
    public BallParticle(Dimension2D gameBoardSize, Point2D position) {
        super(gameBoardSize);
        this.position = position;
    }

    public void move() {
        Point2D midPt = GameBoardUI.MIDPOINT;

        Double x = (position.getX() - midPt.getX()) * speed + midPt.getX();
        Double y = (position.getY() - midPt.getY()) * speed + midPt.getY();

//        if ((x < 0 || x > this.getSize().getWidth()) || (y < 0 || y > this.getSize().getHeight())) {
        if ((x < 0 || x > GameBoardUI.DEFAULT_WIDTH || (y < 0 || y > GameBoardUI.DEFAULT_HEIGHT))) {
            this.isOnBoard = false;
        }

        this.position = new Point2D(x, y);
    }

    @Override
    public Point2D getPosition() {
        return position;
    }
}
