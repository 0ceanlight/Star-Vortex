package de.tum.in.ase.eist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.tum.in.ase.eist.audio.AudioPlayerInterface;
import de.tum.in.ase.eist.car.*;
import de.tum.in.ase.eist.collision.VortexCollision;
import de.tum.in.ase.eist.gameview.GameBoardUI;
import de.tum.in.ase.eist.video.VideoPlayer;
import de.tum.in.ase.eist.video.VideoPlayerInterface;
import de.tum.in.ase.eist.collision.Collision;
import de.tum.in.ase.eist.collision.DefaultCollision;
import javafx.scene.paint.Color;

/**
 * Creates all car objects, detects collisions, updates car positions, notifies
 * player about victory or defeat.
 */
public class GameBoard {

	private static final int NUMBER_OF_SLOW_CARS = 3;
	private static final int NUMBER_OF_TESLA_CARS = 2;

	private static final int MAX_VORTEX_CARS_PER_RING = 6;
	private static final int MIN_VORTEX_CARS_PER_RING = 2;
	private static final Double MIN_VORTEX_SPACING = 2.5 * BallCar.RADIAL_BALL_WIDTH;
	private static final Double MIN_VORTEX_LENGTH = 2 * BallCar.RADIAL_BALL_WIDTH;

	private int score = 0;
//	private int highScore = 0;


	private long gameTick = 0;

	/**
	 * List of all active cars, does not contain player car.
	 */
	private final List<Car> cars = new ArrayList<>();
	private final List<Car> vortexCars = new ArrayList<>();

	/**
	 * The player object with player's car.
	 */
	private final Player player;

	/**
	 * AudioPlayer responsible for handling music and game sounds.
	 */
	private AudioPlayerInterface audioPlayer;
	private VideoPlayerInterface videoPlayer;

	/**
	 * Dimension of the GameBoard.
	 */
	private final Dimension2D size;

	/**
	 * true if game is running, false if game is stopped.
	 */
	private boolean running;

	/**
	 * List of all loser cars (needed for testing, DO NOT DELETE THIS)
	 */
	private final List<Car> loserCars = new ArrayList<>();

	/**
	 * The outcome of this game from the players perspective. The game's outcome is open at the beginning.
	 */
	private GameOutcome gameOutcome = GameOutcome.OPEN;

	/**
	 * Creates the game board based on the given size.
	 *
	 * @param size of the game board
	 */
	public GameBoard(Dimension2D size) {
//		this.videoPlayer = new VideoPlayer();
		this.size = size;
//		FastCar playerCar = new FastCar(size);
		BallCar playerCar = new BallCar(size);
		this.player = new Player(playerCar);
		this.player.setup();
		createCars();
	}

	/**
	 * Creates as many cars as specified by {@link #NUMBER_OF_SLOW_CARS} and adds
	 * them to the cars list.
	 */
	private void createCars() {
		if (gameTick == 0) {
			for (int i = 0; i < NUMBER_OF_SLOW_CARS; i++) {
				this.cars.add(new SlowCar(this.size));
			}
			for (int i = 0; i < NUMBER_OF_TESLA_CARS; i++) {
				this.cars.add(new FastCar(this.size));
			}
		}

		if (gameTick % 20 == 0) {
			if (gameTick >= 40) {
				score++;
//				if (score > highScore) {
//					highScore = score;
//				}
			}

			Double modifier = randomDouble(0.0, 359.0);
			Double startAngle = 0.0;
			Double endAngle = 360.0 - MIN_VORTEX_SPACING;

			for (int i = 0; i < MAX_VORTEX_CARS_PER_RING; i++) {
				if (i < MIN_VORTEX_CARS_PER_RING || randomInt(0, 1) == 1) {
					if (startAngle + MIN_VORTEX_LENGTH <= endAngle) {
						Double len = randomDouble(MIN_VORTEX_LENGTH, angleLength(startAngle, endAngle));
						Double newEnd = startAngle + len;
						this.vortexCars.add(new VortexCar(this.size, Color.WHITE, (startAngle + modifier) % 360, (newEnd + modifier) % 360));
						startAngle += len;
					} else {
						break;
					}
				}
				startAngle += MIN_VORTEX_SPACING;
			}
		}
	}

	private Double angleLength(Double startAngle, Double endAngle) {
		Double length;
		if (startAngle < endAngle) {
			length = endAngle - startAngle;
		} else {
			length = endAngle + (360 - startAngle);
		}
		return length;
	}

	// random number, inclusive
	public Double randomDouble(Double from, Double to) {
		Random randy = new Random();

		return randy.nextDouble(from, to + 1);
	}

	public int randomInt(int from, int to) {
		Random randy = new Random();
		return randy.nextInt(from, to - from + 1);
	}

	public Dimension2D getSize() {
		return size;
	}

	/**
	 * Returns if game is currently running.
	 *
	 * @return true if the game is currently running, false otherwise
	 */
	public boolean isRunning() {
		return this.running;
	}

	/**
	 * Sets whether the game should be currently running.
	 * <p>
	 * Also used for testing on Artemis.
	 *
	 * @param running true if the game should be running
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	public GameOutcome getGameOutcome() {
		return gameOutcome;
	}

	/**
	 * Returns all cars on the game board except the player's car as a list.
	 *
	 * @return the list of all non-player cars
	 */
	public List<Car> getCars() {
		return this.cars;
	}

	public List<Car> getVortexCars() {
		return vortexCars;
	}

	public Car getPlayerCar() {
		return this.player.getCar();
	}

	public AudioPlayerInterface getAudioPlayer() {
		return this.audioPlayer;
	}

	public void setAudioPlayer(AudioPlayerInterface audioPlayer) {
		this.audioPlayer = audioPlayer;
	}

	public VideoPlayerInterface getVideoPlayer() {
		return videoPlayer;
	}

	public void setVideoPlayer(VideoPlayerInterface videoPlayer) {
		this.videoPlayer = videoPlayer;
	}

	/**
	 * Updates the position of each car.
	 */
	public void update() {
		moveCars();
	}

	/**
	 * Starts the game. Cars start to move and background music starts to play.
	 */
	public void startGame() {
//		this.videoPlayer = new VideoPlayer();
		playVideo();
		playMusic();
		this.running = true;
	}

	/**
	 * Stops the game. Cars stop moving and background music stops playing.
	 */
	public void stopGame() {
		stopVideo();
		stopMusic();
		this.running = false;
	}

	/**
	 * Starts the background music.
	 */
	public void playMusic() {
		this.audioPlayer.playBackgroundMusic();
	}

	/**
	 * Stops the background music.
	 */
	public void stopMusic() {
		this.audioPlayer.stopBackgroundMusic();
	}

	public void playVideo() {
		this.videoPlayer.playBackgroundVideo();
	}

//	public void pauseVideo() {
//		this.videoPlayer.pauseBackgroundVideo();
//	}

	public void stopVideo() {
		this.videoPlayer.stopBackgroundVideo();
	}

	/**
	 * @return list of loser cars
	 */
	public List<Car> getLoserCars() {
		return this.loserCars;
	}

	/**
	 * Moves all cars on this game board one step further.
	 */
	public void moveCars() {
		gameTick++;
		createCars();

		// update the positions of the player car and the autonomous cars
		for (Car car : this.cars) {
			car.drive(size);
		}

		for (int i = 0; i < vortexCars.size(); i++) {
			if (vortexCars.get(i) instanceof VortexCar car) {
				car.drive(size);
				if (car.getRadius() >= GameBoardUI.DEFAULT_WIDTH) {
					vortexCars.remove(car);
					i--;
				}
			}
		}

		this.player.getCar().drive(size);

		for (Car car : vortexCars) {
			Collision collision = new VortexCollision(player.getCar(), car);

			if (collision.isCrash()) {
				Car winner = collision.evaluate();
				Car loser = collision.evaluateLoser();
				printWinner(winner);
				loserCars.add(loser);

				this.audioPlayer.playCrashSound();

				loser.crunch();

				stopGame();
				gameOutcome = GameOutcome.LOST;
			}
		}

		// iterate through all cars (except player car) and check if it is crunched
		for (Car car : cars) {
			if (car.isCrunched()) {
				// because there is no need to check for a collision
				continue;
			}

			// TODO Backlog Item 16: Add a new collision type
			/*
			 * Hint: Make sure to create a subclass of the class Collision and store it in
			 * the new Collision package. Create a new collision object and check if the
			 * collision between player car and autonomous car evaluates as expected
			 */

			Collision collision = new DefaultCollision(player.getCar(), car);

			if (collision.isCrash()) {
				Car winner = collision.evaluate();
				Car loser = collision.evaluateLoser();
				printWinner(winner);
				loserCars.add(loser);

				this.audioPlayer.playCrashSound();

				// TODO Backlog Item 11: The loser car is crunched and stops driving
				loser.crunch();

				// TODO Backlog Item 11: The player gets notified when he looses or wins the game
				/*
				 * Hint: you should set the attribute gameOutcome accordingly. Use 'isWinner()'
				 * below for your implementation
				 */
				if (isWinner()) {
					stopGame();
					gameOutcome = GameOutcome.WON;
				}

				if (getPlayerCar().isCrunched()) {
					stopGame();
					gameOutcome = GameOutcome.LOST;
				}
			}
		}
	}

	/**
	 * If all other cars are crunched, the player wins.
	 *
	 * @return true if the game is over and the player won, false otherwise
	 */
	private boolean isWinner() {
		for (Car car : getCars()) {
			if (!car.isCrunched()) {
				return false;
			}
		}
		return true;
	}

	private void printWinner(Car winner) {
		if (winner == this.player.getCar()) {
			System.out.println("The player's car won the collision!");
		} else if (winner != null) {
			System.out.println(winner.getClass().getSimpleName() + " won the collision!");
		} else {
			System.err.println("Winner car was null!");
		}
	}

	public int getScore() {
		return score;
	}

//	public int getHighScore() {
//		return highScore;
//	}
}
