package de.tum.in.ase.eist.gameview;

import de.tum.in.ase.eist.Dimension2D;
import de.tum.in.ase.eist.GameBoard;
import de.tum.in.ase.eist.GameOutcome;
import de.tum.in.ase.eist.Point2D;
import de.tum.in.ase.eist.audio.AudioPlayer;
import de.tum.in.ase.eist.car.Car;
import de.tum.in.ase.eist.car.VortexCar;
import de.tum.in.ase.eist.usercontrol.MouseSteering;
import de.tum.in.ase.eist.video.VideoPlayer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class implements the user interface for steering the player car. The
 * user interface is implemented as a Thread that is started by clicking the
 * start button on the tool bar and stops by the stop button.
 */
public class GameBoardUI extends Canvas {

	/**
	 * The update period of the game in ms, this gives us 25 fps.
	 */
	private static final int UPDATE_PERIOD = 1000 / 25;
	public static final int DEFAULT_WIDTH = 950;
	public static final int DEFAULT_HEIGHT = 600;
	private static final Dimension2D DEFAULT_SIZE = new Dimension2D(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	public static final int ANGLE_360 = 360;


	// vortex attributes
	public static final int EFFECTIVE_RADIUS = DEFAULT_HEIGHT / 3;
	public static final int FADE_IN_RADIUS = DEFAULT_HEIGHT / 26;
	public static final Point2D MIDPOINT = new Point2D(DEFAULT_WIDTH / 2 - 10, DEFAULT_HEIGHT / 2 - 10);


	public static Dimension2D getPreferredSize() {
		return DEFAULT_SIZE;
	}

	/**
	 * Timer responsible for updating the game every frame that runs in a separate
	 * thread.
	 */
	private Timer gameTimer;

	private GameBoard gameBoard;

	private final GameToolBar gameToolBar;

	private MouseSteering mouseSteering;

	private HashMap<String, Image> imageCache;

	public GameBoardUI(GameToolBar gameToolBar) {
		this.gameToolBar = gameToolBar;
		setup();
	}

	public GameBoard getGameBoard() {
		return gameBoard;
	}

	public MouseSteering getMouseSteering() {
		return mouseSteering;
	}

	/**
	 * Removes all existing cars from the game board and re-adds them. Player car is
	 * reset to default starting position. Renders graphics.
	 */
	public void setup() {
		setupGameBoard();
		setupImageCache();
		this.gameToolBar.updateToolBarStatus(false);
		this.addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent moveEvent) -> {
			this.mouseSteering.mouseMoved(moveEvent);
		});
	}

	private void setupGameBoard() {
		Dimension2D size = getPreferredSize();
		this.gameBoard = new GameBoard(size);
		this.gameBoard.setAudioPlayer(new AudioPlayer());
		this.gameBoard.setVideoPlayer(new VideoPlayer());
		widthProperty().set(size.getWidth());
		heightProperty().set(size.getHeight());
		this.mouseSteering = new MouseSteering(this.gameBoard.getPlayerCar(), this);
//		this.addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent moveEvent) -> {
//			this.mouseSteering.mouseMoved(moveEvent);
//		});

//		this.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent clickEvent) -> {
//			this.mouseSteering.mousePressed(clickEvent);
//		});
	}

	private void setupImageCache() {
		this.imageCache = new HashMap<>();
		for (Car car : this.gameBoard.getCars()) {
			// Loads the image into the cache
			getImage(car.getIconLocation());
		}
		String playerImageLocation = this.gameBoard.getPlayerCar().getIconLocation();
		getImage(playerImageLocation);
	}

	/**
	 * Returns the car's image. If no image is present in the cache, a new image is created.
	 *
	 * @param carImageFilePath an image file path that needs to be available in the
	 *                         resources folder of the project
	 */
	private Image getImage(String carImageFilePath) {
		return this.imageCache.computeIfAbsent(carImageFilePath, this::createImage);
	}

	/**
	 * Loads the car's image.
	 *
	 * @param carImageFilePath an image file path that needs to be available in the
	 *                         resources folder of the project
	 */
	private Image createImage(String carImageFilePath) {
		URL carImageUrl = getClass().getClassLoader().getResource(carImageFilePath);
		if (carImageUrl == null) {
			throw new IllegalArgumentException(
					"Please ensure that your resources folder contains the appropriate files for this exercise.");
		}
		return new Image(carImageUrl.toExternalForm());
	}

	/**
	 * Starts the GameBoardUI Thread, if it wasn't running. Starts the game board,
	 * which causes the cars to change their positions (i.e. move). Renders graphics
	 * and updates tool bar status.
	 */
	public void startGame() {
		if (!this.gameBoard.isRunning()) {
			this.gameBoard.startGame();
			this.gameToolBar.updateToolBarStatus(true);
			startTimer();
			paint();
		}
	}

	private void startTimer() {
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				updateGame();
			}
		};
		if (this.gameTimer != null) {
			this.gameTimer.cancel();
		}
		this.gameTimer = new Timer();
		this.gameTimer.scheduleAtFixedRate(timerTask, UPDATE_PERIOD, UPDATE_PERIOD);
	}

	private void updateGame() {
		if (gameBoard.isRunning()) {
			// update score
			this.gameToolBar.getScore().setText("Score: " + this.gameBoard.getScore());
//			this.gameToolBar.getHighScore().setText("High Score: " + this.gameBoard.getScore());
			// updates car positions and re-renders graphics
			this.gameBoard.update();
			// when this.gameBoard.getOutcome() is OPEN, do nothing
			if (this.gameBoard.getGameOutcome() == GameOutcome.LOST) {
				showAsyncAlert("Game over!\nScore: " + gameBoard.getScore());
				this.stopGame();
			} else if (this.gameBoard.getGameOutcome() == GameOutcome.WON_CRUNCHED_OTHERS) {
				showAsyncAlert("Congrats! You won by destroying all other spaceships!");
				this.stopGame();
			} else if (this.gameBoard.getGameOutcome() == GameOutcome.WON_CRASHED_RINGS) {
				showAsyncAlert("Congrats! You won by making it through the vortex!!");
				this.stopGame();
			}
			paint();
		}
	}

	/**
	 * Stops the game board and set the tool bar to default values.
	 */
	public void stopGame() {
		if (this.gameBoard.isRunning()) {
			this.gameBoard.stopGame();
			this.gameToolBar.updateToolBarStatus(false);
			this.gameTimer.cancel();
		}
	}

	/**
	 * Render the graphics of the whole game by iterating through the cars of the
	 * game board at render each of them individually.
	 */
	public void paint() {
		getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
		getGraphicsContext2D().setFill(Color.TRANSPARENT);
		getGraphicsContext2D().fillRect(0, 0, getWidth(), getHeight());

		for (Car ballParticle : this.gameBoard.getBallParticles()) {
			paintCar(ballParticle);
		}

		for (Car vortexCar : this.gameBoard.getVortexCars()) {
			paintVortexCar(vortexCar);
		}

		for (Car car : this.gameBoard.getCars()) {
			paintCar(car);
		}

		// render player car
		paintCar(this.gameBoard.getPlayerCar());
	}

	/**
	 * Show image of a car at the current position of the car.
	 *
	 * @param car to be drawn
	 */
	private void paintCar(Car car) {
		Point2D carPosition = car.getPosition();

		getGraphicsContext2D().drawImage(getImage(car.getIconLocation()), carPosition.getX(),
				carPosition.getY(), car.getSize().getWidth(), car.getSize().getHeight());
	}

	 public void paintBallCar(Car car) {
		Point2D carPosition = car.getPosition();
		Double w = car.getSize().getWidth();
		Double h = car.getSize().getHeight();

		getGraphicsContext2D().drawImage(getImage(car.getIconLocation()), carPosition.getX() - w / 2,
				carPosition.getY() - h / 2, w, h);
	}

//	private void paintBallParticle(Car car) {
//		Point2D pos = car.getPosition();
//		this.getGraphicsContext2D().setStroke(Color.WHITE);
//
////		this.getGraphicsContext2D().strokeArc(pos.getX(), pos.getY(), 2.4, 2.4, 0, 360, ArcType.ROUND);
//
//	}

	private void paintVortexCar(Car car) {
		if (car instanceof VortexCar vortexCar) {
			Color c = new Color(1.0, 1.0, 1.0, 1.0);
			int mpX = (int) MIDPOINT.getX();
			int mpY = (int) MIDPOINT.getY();
			drawRingSegment(mpX, mpY, vortexCar.getRadius(), vortexCar.getStartAngle(), vortexCar.getEndAngle(), vortexCar.getWidth(), c);
		}
	}

	private void drawRingSegment(int x, int y, int radius, double startAngle, double endAngle, Double width, Color color) {
		this.getGraphicsContext2D().setLineWidth(width);
		this.getGraphicsContext2D().setStroke(color);

		Double al = angleLength(startAngle, endAngle);
		this.getGraphicsContext2D().strokeArc(x - radius, y - radius, radius * 2, radius * 2, startAngle, al, ArcType.OPEN);
	}

	private Double angleLength(Double startAngle, Double endAngle) {
		Double length;
		if (startAngle < endAngle) {
			length = endAngle - startAngle;
		} else {
			length = endAngle + (ANGLE_360 - startAngle);
		}
		return length;
	}


	/**
	 * Method used to display alerts in moveCars().
	 *
	 * @param message you want to display as a String
	 */
	private void showAsyncAlert(String message) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText(message);
			alert.showAndWait();
			this.setup();
		});
	}
}
