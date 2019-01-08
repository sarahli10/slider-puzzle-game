// Authors: Dr. Mark Lanthier, Sarah Li

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PuzzleButton extends Button {
	SliderPuzzle puzzle;
	
	int initRow;
	int initCol;
	int curRow;
	int curCol;
	boolean isBlank;
	
	public PuzzleButton(SliderPuzzle sp, int r, int c) {
		puzzle = sp; 
		initRow = r;
		initCol = c;
		setPrefSize(187, 187);
		setPadding(new Insets(0, 0, 0, 0));
		relocate(c * 188, r * 188);
		
		curRow = r;
		curCol = c;
		isBlank = false;
		
		setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				sp.puzzleButtonClick(initRow, initCol);
			}
		});
	}
	
	public void loadImage(String fileName) {
		isBlank = false;
		setGraphic(new ImageView(new Image(getClass().getResourceAsStream(fileName))));
	}

	public void setBlank() {
		isBlank = true;
		setGraphic(new ImageView(new Image(getClass().getResourceAsStream("BLANK.png"))));
	}

	public void reset() {
		curRow = initRow;
		curCol = initCol;
		relocate(curCol * 188, curRow * 188);
		isBlank = false;
	}

	public void setPos(int r, int c) {
		curRow = r;
		curCol = c;
		relocate(c * 188, r * 188);
	}
}
