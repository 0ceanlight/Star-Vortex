package de.tum.in.ase.eist.car;

import de.tum.in.ase.eist.Dimension2D;

public class VortexCar extends Car {

    // TODO: diff colors
//    private final Color color;
    private static final int SPEED = 5;
    private double width = 1.0;

    private int prevRadius = 0;
    private int radius;
    private final Double startAngle;
    private final Double endAngle;
    private final Double widthMod = 15000.0;
    private final Double radMod = 100.0;

    public VortexCar(Dimension2D gameBoardSize, Double startAngle, Double endAngle, int fadeInRadius) {
        super(gameBoardSize);
//        this.color = color;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.radius = fadeInRadius;
    }

    @Override
    public void drive(Dimension2D gameBoardSize) {
        if (this.isCrunched()) {
            return;
        }

        prevRadius = radius;
        radius += (this.SPEED * radius) / radMod;
        width += radius * radius / widthMod;
    }

//    public Color getColor() {
//        return color;
//    }

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
