package de.tum.in.ase.eist.collision;

import de.tum.in.ase.eist.car.BallCar;
import de.tum.in.ase.eist.car.Car;
import de.tum.in.ase.eist.car.VortexCar;

public class VortexCollision extends Collision {

    public VortexCollision(Car car1, Car car2) {
        super(car1, car2);
    }

    public boolean detectCollision() {
        // check if ball inside radius of vortexCar
        VortexCar vortexCar;
        BallCar ballCar;
        if (getCar1() instanceof VortexCar vc && getCar2() instanceof BallCar bc) {
            vortexCar = vc;
            ballCar = bc;
        } else if (getCar2() instanceof VortexCar vc && getCar1() instanceof BallCar bc) {
            vortexCar = vc;
            ballCar = bc;
        } else {
            return false;
        }

        // collision on two conditions...
        boolean hasCollided = false;
        // 1: ball angle within vortex angle
        if (vortexCar.getStartAngle() < vortexCar.getEndAngle()) {
            hasCollided = ballCar.getRadialPos() >= vortexCar.getStartAngle() && ballCar.getRadialPos() <= vortexCar.getEndAngle();
        } else {
            // check interval around angle 0
            hasCollided = (ballCar.getRadialPos() - BallCar.RADIAL_BALL_WIDTH / 2) <= vortexCar.getEndAngle() || (ballCar.getRadialPos() + BallCar.RADIAL_BALL_WIDTH / 2) >= vortexCar.getStartAngle();
        }
        // 2: vortex radius passed through ball radius
        hasCollided &= vortexCar.getPrevRadius() <= ballCar.getRadius() && vortexCar.getRadius() >= ballCar.getRadius();
        return hasCollided;
    }

    public Car evaluate() {
        // vortex always wins
        if (getCar1() instanceof VortexCar) { // swapped > to <
            return getCar1();
        } else {
            return getCar2();
        }
    }
}
