package de.tum.in.ase.eist.car;

import de.tum.in.ase.eist.Dimension2D;
import de.tum.in.ase.eist.gameview.GameBoardUI;

import javafx.scene.paint.Color;

public class VortexCar extends Car {

    // TODO: diff colors
    private final Color color;
    private final int speed = 7;
    private final int MAX_WIDTH = 15;
    private double width = 1.0;

    private int prevRadius = 0;
    private int radius = GameBoardUI.FADE_IN_RADIUS;
    private final Double startAngle;
    private final Double endAngle;


    public VortexCar(Dimension2D gameBoardSize) {
        super(gameBoardSize);
        this.color = Color.WHITE;
        this.startAngle = 340.0;
        this.endAngle = 5.0;
    }

    public VortexCar(Dimension2D gameBoardSize, Color color, Double startAngle, Double endAngle) {
        super(gameBoardSize);
        this.color = color;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
    }

    @Override
    public void drive(Dimension2D gameBoardSize) {
        if (this.isCrunched()) {
            return;
        }

        prevRadius = radius;
        radius += (this.speed * radius) / 100.0;
        width += radius * radius / 13000.0;
    }

    public Color getColor() {
        return color;
    }

    public int getRadius() {
        return radius;
    }

    public int getPrevRadius() {
        return prevRadius;
    }

    public Double getWidth() {
        return width;
    }

    public Double getStartAngle() {
        return startAngle;
    }

    public Double getEndAngle() {
        return endAngle;
    }
}
