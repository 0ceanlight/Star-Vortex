package de.tum.in.ase.eist;

import de.tum.in.ase.eist.gameview.GameBoardUI;
import de.tum.in.ase.eist.gameview.GameToolBar;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;

/**
 * Starts the Bumpers Application, loads the GameToolBar and GameBoardUI. This
 * class is the root of the JavaFX Application.
 *
 * @see Application
 */
public class BumpersApplication extends Application {

	private static final int GRID_LAYOUT_PADDING = 5;
	private static final int GRID_LAYOUT_PREF_HEIGHT = 350;
	private static final int GRID_LAYOUT_PREF_WIDTH = 505;

	/**
	 * Starts the Bumpers Window by setting up a new tool bar, a new user interface
	 * and adding them to the stage.
	 *
	 * @param primaryStage the primary stage for this application, onto which the
	 *                     application scene can be set.
	 */
	@Override
	public void start(Stage primaryStage) {
		// the tool bar object with start and stop buttons
		GameToolBar toolBar = new GameToolBar();
		GameBoardUI gameBoardUI = new GameBoardUI(toolBar);
		toolBar.initializeActions(gameBoardUI);

		StackPane stack = new StackPane();

		stack.getChildren().add(gameBoardUI.getGameBoard().getVideoPlayer().getMediaView());
//		var videoSourceUrl = new File(VIDEO_PATH).toURI().toString();
//		Media media = new Media(videoSourceUrl);
//		MediaView mediaPlayer = new MediaView(new MediaPlayer(media));
//		mediaPlayer.getMediaPlayer().play();
//		stack.getChildren().add(mediaPlayer);

		Pane gridLayout = createLayout(gameBoardUI, toolBar);
		stack.getChildren().add(gridLayout);

		// scene and stages
		Scene scene = new Scene(stack);
		primaryStage.setTitle("Bumpers");
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(closeEvent -> gameBoardUI.stopGame());
		primaryStage.show();
		showAsyncAlert("Welcome to the Space Vortex\n\nYour mission is to successfully guide the orb through the rings\nand avoid enemy spaceships on the way\n - avoid the vortex rings\n - hit enemies on the right\n - control the orb by moving your mouse\n - here we go...");
	}

	private void showAsyncAlert(String message) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText(message);
			alert.showAndWait();
		});
	}

	/**
	 * Creates a new {@link Pane} that arranges the game's UI elements.
	 */
	private static Pane createLayout(GameBoardUI gameBoardUI, GameToolBar toolBar) {
		// GridPanes are divided into columns and rows, like a table
		GridPane gridLayout = new GridPane();
		gridLayout.setPrefSize(GRID_LAYOUT_PREF_WIDTH, GRID_LAYOUT_PREF_HEIGHT);
		gridLayout.setVgap(GRID_LAYOUT_PADDING);
		gridLayout.setPadding(new Insets(GRID_LAYOUT_PADDING));

		// add all components to the gridLayout
		// second parameter is column index, second parameter is row index of grid
		gridLayout.add(gameBoardUI, 0, 1);
		gridLayout.add(toolBar, 0, 0);

		return gridLayout;
	}

	/**
	 * The whole game will be executed through the launch() method.
	 * <p>
	 * Use {@link Bumpers#main(String[])} to run the Java application.
	 */
	public static void startApp(String[] args) {
		launch(args);
	}
}
