package de.tum.in.ase.eist.collision;

import de.tum.in.ase.eist.car.Car;

/**
 * This class defines the behavior when two cars collide.
 */
public abstract class Collision {

    private final Car car1;
    private final Car car2;
    private final boolean crash;

    public Collision(Car car1, Car car2) {
        this.car1 = car1;
        this.car2 = car2;
        this.crash = detectCollision();
    }

    public boolean isCrash() {
        return crash;
    }

	protected Car getCar1() {
		return car1;
	}

	protected Car getCar2() {
		return car2;
	}

    public abstract boolean detectCollision();

    /**
     * Evaluates winner of the collision.
     *
     * @return winner Car
     */
    public abstract Car evaluate();

    /**
     * Evaluates loser of the collision.
     *
     * @return loser Car
     */
    public Car evaluateLoser() {
        Car winner = evaluate();
        if (this.car1 == winner) {
            return this.car2;
        }
        return this.car1;
    }
}
