package de.tum.in.ase.eist;

import de.tum.in.ase.eist.audio.AudioPlayerInterface;
import de.tum.in.ase.eist.car.*;
import de.tum.in.ase.eist.collision.Collision;
import de.tum.in.ase.eist.collision.DefaultCollision;
import de.tum.in.ase.eist.collision.VortexCollision;
import de.tum.in.ase.eist.gameview.GameBoardUI;
import de.tum.in.ase.eist.video.VideoPlayerInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Creates all car objects, detects collisions, updates car positions, notifies
 * player about victory or defeat.
 */
public class GameBoard {
	private static final int PTS_PER_SHIP = 3;
	private static final int CAR_INFREQUENCY = 6; // higher - less frequent, >= 1
	private static final Double ANGLE_360_DEG = 360.0;
	private static final int PARTICLE_FREQUENCY = 5;
	private static final int BEGIN_AFTER = 3;
	private static final int END_BEFORE = 3;

	private static final int SCORE_TO_WIN = 12;
	private static final int VORTEX_SPACING = 40;
	private static final int NUMBER_OF_SLOW_CARS = 3;
	private int numSlowCars = 0;

	private static final int NUMBER_OF_TESLA_CARS = 2;
	private int numTeslaCars = 0;

	private static final int MAX_VORTEX_CARS_PER_RING = 6;
	private static final int MIN_VORTEX_CARS_PER_RING = 2;
	private static final Double MIN_VORTEX_SPACING = 4 * BallCar.RADIAL_BALL_WIDTH;
	private static final Double MIN_VORTEX_LENGTH = 2 * BallCar.RADIAL_BALL_WIDTH;

	private static final Random RANDY = new Random();


	private int score = 0;
//	private int highScore = 0;


	private long gameTick = 0;

	/**
	 * List of all active cars, does not contain player car.
	 */
	private final List<Car> cars = new ArrayList<>();
	private final List<Car> vortexCars = new ArrayList<>();
	private final List<BallParticle> ballParticles = new ArrayList<>();

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
		// ball particles
		if (gameTick % PARTICLE_FREQUENCY == 0) {
			ballParticles.add(new BallParticle(this.size, player.getCar().getPosition()));
		}

		if (gameTick % VORTEX_SPACING == 0) {
			if (gameTick >= VORTEX_SPACING * BEGIN_AFTER) {
				score++;
//				if (score > highScore) {
//					highScore = score;
//				}
			}

			if (score + END_BEFORE < SCORE_TO_WIN && gameTick >= VORTEX_SPACING * 2) {
				addVortexCar();
			}

			generateSpaceships();
		}
	}

	private void generateSpaceships() {
		switch (randomInt(0, CAR_INFREQUENCY)) {
			case 0:
				if (numSlowCars < NUMBER_OF_SLOW_CARS) {
					this.cars.add(new SlowCar(this.size));
					numSlowCars++;
				}
				break;
			case 1:
				if (numTeslaCars < NUMBER_OF_TESLA_CARS) {
					this.cars.add(new FastCar(this.size));
					numTeslaCars++;
				}
				break;
			default:
				break;
		}
	}

	public void addVortexCar() {
		Double modifier = randomDouble(0.0, ANGLE_360_DEG - 1);
		Double startAngle = 0.0;
		Double endAngle = ANGLE_360_DEG - MIN_VORTEX_SPACING;

		for (int i = 0; i < MAX_VORTEX_CARS_PER_RING; i++) {
			if (i < MIN_VORTEX_CARS_PER_RING || randomInt(0, 1) == 1) {
				if (startAngle + MIN_VORTEX_LENGTH <= endAngle) {
					Double len = randomDouble(MIN_VORTEX_LENGTH, angleLength(startAngle, endAngle));
					Double newEnd = startAngle + len;

					Double sa = (startAngle + modifier) % ANGLE_360_DEG;
					Double ea = (newEnd + modifier) % ANGLE_360_DEG;

					this.vortexCars.add(new VortexCar(this.size, sa, ea));
					startAngle += len;
				} else {
					break;
				}
			}
			startAngle += MIN_VORTEX_SPACING;
		}
	}

	private Double angleLength(Double startAngle, Double endAngle) {
		Double length;
		if (startAngle < endAngle) {
			length = endAngle - startAngle;
		} else {
			length = endAngle + (ANGLE_360_DEG - startAngle);
		}
		return length;
	}

	// random number, inclusive
	public Double randomDouble(Double from, Double to) {
		return RANDY.nextDouble(from, to + 1);
	}

	public int randomInt(int from, int to) {
		return RANDY.nextInt(from, to - from + 1);
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
		gameTick++;

		moveCars();

		if (score >= SCORE_TO_WIN) {
			stopGame();
			gameOutcome = GameOutcome.WON_CRASHED_RINGS;
		}
	}

	/**
	 * Starts the game. Cars start to move and background music starts to play.
	 */
	public void startGame() {
//		this.videoPlayer = new VideoPlayer();
		playVideo();
		playMusic();
		this.running = true;
		this.getCars().clear();
		numSlowCars = 0;
		numTeslaCars = 0;
	}

	/**
	 * Stops the game. Cars stop moving and background music stops playing.
	 */
	public void stopGame() {
		stopVideo();
		stopMusic();
		this.running = false;

		numSlowCars = 0;
		numTeslaCars = 0;
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
		createCars();

		for (int i = 0; i < ballParticles.size(); i++) {
			BallParticle bp = ballParticles.get(i);

			if (bp.isOnBoard()) {
				bp.drive(this.size);
			} else {
				ballParticles.remove(bp);
				i--;
			}
		}

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
		for (int i = 0; i < cars.size(); i++) {
			Car car = cars.get(i);

//			if (car.isCrunched()) {
//				// because there is no need to check for a collision
//				continue;
//			}

			Collision collision = new DefaultCollision(player.getCar(), car);

			if (collision.isCrash()) {
				Car winner = collision.evaluate();
				Car loser = collision.evaluateLoser();
				printWinner(winner);
				loserCars.add(loser);

				this.audioPlayer.playCrashSound();

				loser.crunch();

				if (isWinner()) {
					stopGame();
					gameOutcome = GameOutcome.WON_CRUNCHED_OTHERS;
				}

				if (getPlayerCar().isCrunched()) {
					stopGame();
					gameOutcome = GameOutcome.LOST;
				} else {
					cars.remove(loser);
					this.score += PTS_PER_SHIP;
					i--;
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
		if (cars.isEmpty() && numTeslaCars == NUMBER_OF_TESLA_CARS && numSlowCars == NUMBER_OF_SLOW_CARS) {
			return true;
		}

		return false;
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

	public List<BallParticle> getBallParticles() {
		return ballParticles;
	}

//	public int getHighScore() {
//		return highScore;
//	}
}
