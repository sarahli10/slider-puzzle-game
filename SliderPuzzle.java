// Authors: Dr. Mark Lanthier, Sarah Li

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SliderPuzzle extends Application {
	private int selection;
	private int blankRow;
	private int blankCol;
	private int numSec;
	private boolean solving;

	PuzzleButton[][] slides;
	Label thumbnail;
	ListView<String> nameList;
	Button startStopButton;
	TextField timeElapsedField;

	Timeline updateTimer;

	public void start(Stage primaryStage) {
		solving = false;
		String[] names = { "Lego", "Numbers", "Pets", "Scenery" }; // name of puzzle
		selection = 0;

		Pane aPane = new Pane();
		aPane.setStyle("-fx-padding: 10 10");

		// creating the thumbnail
		thumbnail = new Label();
		thumbnail.setGraphic(
				new ImageView(new Image(getClass().getResourceAsStream(names[selection] + "_Thumbnail.png"))));
		thumbnail.setStyle("-fx-padding: 10 10");
		thumbnail.relocate(750, 0);

		// creating the options ListView
		nameList = new ListView<String>();
		nameList.setItems(FXCollections.observableArrayList(names));
		nameList.getSelectionModel().select(0); // initial = Lego
		nameList.setStyle("-fx-padding: 10 10");
		nameList.relocate(760, 210);
		nameList.setPrefSize(187, 200); // assumed the size for this one, not specified in the instructions

		nameList.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseEvent) {
				String selectedName = nameList.getSelectionModel().getSelectedItem();
				for (int i = 0; i < 4; i++) {
					if (selectedName.equals(names[i])) {
						selection = i;
						thumbnail.setGraphic(new ImageView(
								new Image(getClass().getResourceAsStream(names[selection] + "_Thumbnail.png"))));
					}
				}
			}
		});

		// fullImage.setGraphic(
		// new ImageView(new Image(getClass().getResourceAsStream(names[selection] +
		// "_Thumbnail.png"))));

		aPane.getChildren().addAll(nameList, thumbnail);

		slides = new PuzzleButton[4][4];

		for (int r = 0; r < 4; r++)
			for (int c = 0; c < 4; c++) {
				slides[r][c] = new PuzzleButton(this, r, c);
				slides[r][c].loadImage("BLANK.png");
				aPane.getChildren().add(slides[r][c]);
			}

		// creating the Start/Stop Button
		startStopButton = new Button();
		startStopButton.setText("Start");
		startStopButton.relocate(760, 420);
		startStopButton.setPrefSize(187, 30);
		startStopButton.setStyle("-fx-text-fill: WHITE; -fx-base: DARKGREEN");

		// creating the Time Label
		Label timeLabel = new Label();
		timeLabel.setText("Time: ");
		timeLabel.relocate(760, 460);

		// creating the time elapsed text field
		timeElapsedField = new TextField();
		timeElapsedField.setText("0:00");
		timeElapsedField.setAlignment(Pos.CENTER_LEFT);
		timeElapsedField.relocate(800, 458);
		timeElapsedField.setPrefSize(148, 25);

		numSec = 0;
		updateTimer = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				numSec++;
				showTimeElapsed(numSec);
			}

			public void showTimeElapsed(int numSec) {
				int displayMin = (int) (numSec / 60);
				int displaySec = numSec % 60;
				timeElapsedField.setText(String.format("%d:%02d", displayMin, displaySec));
			}

		}));
		updateTimer.setCycleCount(Timeline.INDEFINITE);

		// pressed "Start"
		startStopButton.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseEvent) {
				if ("Start".equals(startStopButton.getText())) {
					updateTimer.play();
					thumbnail.setDisable(true); // disable the thumbnail
					nameList.setDisable(true);
					solving = true;
					numSec = 0;

					startStopButton.setText("Stop");
					startStopButton.setStyle("-fx-text-fill: WHITE; -fx-base: DARKRED");
					timeElapsedField.setText("0:00");

					blankRow = (int) (Math.random() * 4);
					blankCol = (int) (Math.random() * 4);
					for (int r = 0; r < 4; r++)
						for (int c = 0; c < 4; c++) {
							String fileName = names[selection] + "_" + r + c + ".png";
							slides[r][c].loadImage(fileName);
						}
					slides[blankRow][blankCol].setBlank();

					for (int i=0; i<1000; i++)
						shuffleSlides();

				} else { // text on button = Stop
					endGame();
					timeElapsedField.setText("0:00");

					for (int r = 0; r < 4; r++)
						for (int c = 0; c < 4; c++) {
							slides[r][c].reset();
							slides[r][c].setBlank();
						}
				}
			}

		});

		aPane.getChildren().addAll(startStopButton, timeLabel, timeElapsedField);

		primaryStage.setTitle("Slider Puzzle Game");
		primaryStage.setScene(new Scene(aPane, 956, 760));
		primaryStage.show();
	}

	public void puzzleButtonClick(int row, int col) {
		if (!solving)
			return;
		if (slides[row][col].isBlank) {
			return;
		}

		int rowDiff = Math.abs(slides[row][col].curRow - slides[blankRow][blankCol].curRow);
		int colDiff = Math.abs(slides[row][col].curCol - slides[blankRow][blankCol].curCol);
		if (rowDiff==0 && colDiff==1) {
			swapSlides(slides[row][col], slides[blankRow][blankCol]);

			if (isOver())
				endGame();
			return;
		}
		if (colDiff==0 && rowDiff==1) {
			swapSlides(slides[row][col], slides[blankRow][blankCol]);

			if (isOver())
				endGame();
			return;
		}
	}

	private void endGame() {
		updateTimer.stop();

		thumbnail.setDisable(false);
		nameList.setDisable(false);
		solving = false;

		startStopButton.setText("Start");
		startStopButton.setStyle("-fx-text-fill: WHITE; -fx-base: DARKGREEN");

		timeElapsedField.setText(String.format("Solved in %d secs", numSec));
	}

	private boolean isOver() {
		for (int r = 0; r < 4; r++)
			for (int c = 0; c < 4; c++) {
				if (slides[r][c].curRow != slides[r][c].initRow)
					return false;
				if (slides[r][c].curCol != slides[r][c].initCol)
					return false;
			}
		return true;
	}

	private void swapSlides(PuzzleButton x, PuzzleButton y) {
		int tempRow = x.curRow;
		int tempCol = x.curCol;
		x.setPos(y.curRow, y.curCol);
		y.setPos(tempRow, tempCol);
	}

	private void shuffleSlides() {
		int br = slides[blankRow][blankCol].curRow;
		int bc = slides[blankRow][blankCol].curCol;

		PuzzleButton randSlize = null;
		for (; randSlize == null;) {
			int dir = (int) (Math.random() * 4);
			switch (dir) {
				case 0: // LEFT
					if (bc == 0)
						continue;
					randSlize = findSlize(br, bc - 1);
					break;
				case 1: // RIGHT
					if (bc == 3)
						continue;
					randSlize = findSlize(br, bc + 1);
					break;
				case 2: // UP
					if (br == 0)
						continue;
					randSlize = findSlize(br - 1, bc);
					break;
				case 3: // DOWN
					if (br == 3)
						continue;
					randSlize = findSlize(br + 1, bc);
					break;
			}
		}
		swapSlides(randSlize, slides[blankRow][blankCol]);
	}

	private PuzzleButton findSlize(int rr, int cc) {
		for (int r = 0; r < 4; r++)
			for (int c = 0; c < 4; c++) {
				if (slides[r][c].curRow == rr && slides[r][c].curCol == cc)
					return slides[r][c];
			}
		return null;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
