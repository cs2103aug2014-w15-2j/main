package userInterface;

import infrastructure.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import model.Task;
import model.TimeInterval;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LayoutManager {
	
	public static VBox getHelpBox() {
		String[] shortcuts = {"", "Ctrl + 1", "Ctrl + 2", "Ctrl + 3", "Ctrl + 4",
				"", "Ctrl + C", "Ctrl + R", "Ctrl + U", "Ctrl + D", "Ctrl + F", "Ctrl + M",
				"", "Alt + A", "Alt + C", "Alt + D", "Alt + I", "Alt + P", "Alt + T",
				""};
		String[] descriptions = {"", "Switch to \"TODO\" page", "Switch to \"FINISHED\" page", "Switch to \"TRASHED\" page", "Switch to \"HELP\" page",
				"", "Insert command: add", "Insert command: display", "Insert command: update", "Insert command: delete", "Insert command: search", "Insert command: reload model",
				"", "Insert tag: <DATE> or </DATE>", "Insert tag: <COMMAND> or </COMMAND>", "Insert tag: <DESCRIPTION> or </DESCRIPTION>", "Insert tag: <INDEX> or </INDEX>", "Insert tag: <PRIORITY> or </PRIORITY>", "Insert tag: <TAG> or </TAG>",
				""}; 
		
		
		VBox overallBox = new VBox();
		HBox emptyRow = new HBox();
		emptyRow.setPrefHeight(17);
		HBox rowContainer = new HBox();
		rowContainer.setAlignment(Pos.CENTER);
		VBox emptyColumn = new VBox();
		emptyColumn.setPrefWidth(5);
		ScrollPane helpPane = new ScrollPane();
		VBox helpBox = new VBox();
		helpBox.setAlignment(Pos.CENTER);
		helpPane.getStyleClass().add("helpScrollView");
		
		helpPane.setStyle("-fx-background-color: white;"
				+ "-fx-padding: 0 10 0 30;"
				+ Constant.CSS_STYLE_SHADOW);
		helpPane.setPrefSize(935, 430);
		helpBox.getChildren().add(getHelpTitleBox("Shortcuts"));
		
		assert(shortcuts.length == descriptions.length);
		for (int i = 0; i < shortcuts.length; i++) {
			helpBox.getChildren().add(getHelpRow(shortcuts[i], descriptions[i]));
		}
		
//		helpBox.getChildren().add(getHelpTitleBox("Sample Commands"));
		
		helpPane.setContent(helpBox);
		rowContainer.getChildren().addAll(emptyColumn, helpPane);
		overallBox.getChildren().addAll(emptyRow, rowContainer);
		return overallBox;
	}

	private static VBox getHelpTitleBox(String title) {
		VBox row = new VBox();
		row.setStyle("-fx-background-color:white;");
		row.setPrefWidth(885);
		Label titleLabel = new Label(title);
		
		titleLabel.setStyle("-fx-font: 30px \"Akagi\";"
				+ "-fx-padding: 60 10 10 0;"
				+ "-fx-background-color: white;");
		HBox line = new HBox();
		line.setPrefWidth(855);
		line.setPrefHeight(1);
		line.setStyle("-fx-background-color: black;");
		row.getChildren().addAll(titleLabel, line);
		return row;
	}
	
	private static HBox getHelpRow(String shortcut, String description) {
		HBox row = new HBox();
		row.setPrefHeight(30);
		row.setStyle("-fx-padding: 0 10 0 5;"
				+ "-fx-background-color: white;");
		VBox leftBox = new VBox();
		leftBox.setAlignment(Pos.CENTER_LEFT);
		leftBox.setStyle("-fx-padding: 0 50 0 0;");
		leftBox.setPrefWidth(150);
		Label leftLabel = new Label(shortcut);
		leftLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
		leftLabel.setStyle(""
				+ "-fx-text-fill: #005587");
		leftBox.getChildren().add(leftLabel);
		
		VBox rightBox = new VBox();
		Label rightLabel = new Label(description);
		rightBox.setAlignment(Pos.CENTER_LEFT);
		rightLabel.setStyle("-fx-font: 18px \"Akagi\"");
		rightBox.getChildren().add(rightLabel);
		
		row.getChildren().addAll(leftBox, rightBox);
		return row;
	}
	
	//@author A0119379R
	public static GridPane getTagPaneForTask(Task task) {
		GridPane tagPane = new GridPane();
		tagPane.setStyle(Constant.CSS_STYLE_TAG_PANE);
		tagPane.setHgap(10);
		ArrayList<String> tags = task.getTag();
		int columnIndex = 0;
		for (String tag : tags) {
			Label tagLabel = new Label(tag);
			
			int colorOffset = 0;
			for (char c : tag.toCharArray()) {
				colorOffset += c;
			}
			
			colorOffset = colorOffset%Constant.COLORS_TAG.length;
			tagLabel.setStyle(String.format(Constant.CSS_STYLE_TAG_LABEL, Constant.COLORS_TAG[colorOffset]));
			tagPane.add(tagLabel, columnIndex, 0);
			columnIndex ++;
		}
		return tagPane;
	}

	//@author A0119447Y
	public static GridPane getTaskPane(ArrayList<Task> taskList, double width) {
		GridPane taskPane = new GridPane();
		int row = 1;
		int index = 1;
		taskPane.getColumnConstraints().add(new ColumnConstraints(5));
		
		GridPane placeHolderPane = LayoutManager.getEmptyPane(width - 55);
		taskPane.add(placeHolderPane, 1, 0, 2, 1);
		LayoutManager.setDisplayRow(taskPane, Constant.GRID_ROW_HEIGHT / 2);
		
		if (taskList != null) {
			for (Task task : taskList) {
				int subRow = 0;
				GridPane panePane = new GridPane();
				String bannerColor = LayoutManager.getBannerColor(task);
				GridPane priorityPane = LayoutManager.getPriorityPane(bannerColor, index, width - 55);
				panePane.add(priorityPane, 0, subRow, 2, 1);
				LayoutManager.setDisplayRow(panePane, Constant.GRID_ROW_HEIGHT);
				subRow ++;
				
				GridPane contentPane = LayoutManager.getContentPane(task, width - 55);
				
				panePane.add(contentPane, 0, subRow, 1, 2);
				LayoutManager.setDisplayRow(panePane, Constant.GRID_ROW_HEIGHT * 2 + 11);
				panePane.setStyle(Constant.CSS_STYLE_SHADOW);
				
				taskPane.add(panePane, 1, row, 2, 1);
				LayoutManager.setDisplayRow(taskPane, Constant.GRID_ROW_HEIGHT * 3 + 11);
				
				GridPane emptyPane = LayoutManager.getEmptyPane(width - 55);
				taskPane.add(emptyPane, 1, row + 1, 2, 1);
				LayoutManager.setDisplayRow(taskPane, Constant.GRID_ROW_HEIGHT);
				row += 2;
				index ++;
			}
		}
		return taskPane;
	}

	//@author A0119379R
	public static GridPane getEmptyPane(double width) {
		GridPane emptyPane = new GridPane();
		emptyPane.setStyle(Constant.CSS_STYLE_EMPTY_PANE);
		emptyPane.setPrefWidth(width);
		return emptyPane;
	}

	//@author A0119379R
	public static GridPane getContentPane(Task task, double width) {
		String bodyColor = LayoutManager.getBodyColor(task);
		GridPane contentPane = LayoutManager.getContentPane(bodyColor, width);
		
	
		int subRow = 0;
	
		
		Label description = LayoutManager.getDescriptionLabel(task, LayoutManager.calculateDescriptionWidth(width));
		
		contentPane.add(description, 0, subRow, 2, 1);
		LayoutManager.setDisplayRow(contentPane, Constant.GRID_ROW_HEIGHT);
		subRow ++;
		
		HBox timeBox = LayoutManager.getTimeBoxForTask(task);
		if (timeBox != null) {
			contentPane.add(timeBox, 1, 0, 2, 2);
		}
		
		GridPane tagPane = getTagPaneForTask(task);
		contentPane.add(tagPane, 0, subRow, 2, 1);
		LayoutManager.setDisplayRow(contentPane, Constant.GRID_ROW_HEIGHT - 5);
		subRow ++;
		return contentPane;
	}

	//@author A0119447Y
	public static String getBodyColor(Task task) {
		switch (task.getPriority()) {
			case Constant.PRIORITY_HIGH:
				return Constant.COLOR_BODY_PRIORITY_HIGH;
	
			case Constant.PRIORITY_MEDIUM:
				return Constant.COLOR_BODY_PRIORITY_MEDIUM;
				
			case Constant.PRIORITY_LOW:
				return Constant.COLOR_BODY_PRIORITY_LOW;
				
			default:
				return Constant.COLOR_BODY_DEFAULT;
		}
	}

	//@author A0119447Y
	public static String getBannerColor(Task task) {
		switch (task.getPriority()) {
			case Constant.PRIORITY_HIGH:
				return Constant.COLOR_BANNER_PRIORITY_HIGH;
	
			case Constant.PRIORITY_MEDIUM:
				return Constant.COLOR_BANNER_PRIORITY_MEDIUM;
				
			case Constant.PRIORITY_LOW:
				return Constant.COLOR_BANNER_PRIORITY_LOW;
				
			default:
				return Constant.COLOR_BANNER_DEFAULT;
		}
	}

	//@author A0119447Y
	public static GridPane getPriorityPane(String bannerColor, int index, double width) {
		GridPane priorityPane = new GridPane();
		priorityPane.setAlignment(Pos.CENTER_RIGHT);
		priorityPane.setStyle(String.format(Constant.CSS_STYLE_PRIORITY_PANE, bannerColor));
		priorityPane.setPrefWidth(width);
		
		Label priorityLabel = new Label(String.format("%d", index));
		priorityLabel.setStyle(Constant.CSS_STYLE_PRIORITY_LABEL);
		priorityLabel.setPrefWidth(width);
		priorityPane.getChildren().add(priorityLabel);
		return priorityPane;
	}

	//@author A0119379R
	public static Label getDescriptionLabel(Task task, double width) {
		Label descriptionLabel = new Label(task.getDescription());
		descriptionLabel.setStyle(Constant.CSS_STYLE_DESCRIPTION_LABEL);
		descriptionLabel.setPrefWidth(width);
		return descriptionLabel;
	}

	//@author A0119447Y
	public static GridPane getContentPane(String bodyColor, double width) {
		GridPane contentPane = new GridPane();
		contentPane.setGridLinesVisible(false);
	    contentPane.getColumnConstraints().add(new ColumnConstraints(LayoutManager.calculateDescriptionWidth(width)));
		contentPane.getColumnConstraints().add(new ColumnConstraints(LayoutManager.calculateTimeWidth(width)));
		contentPane.setStyle(String.format(Constant.CSS_STYLE_CONTENT_PANE, bodyColor));
		contentPane.setPrefWidth(width);
		return contentPane;
	}

	//@author A0119379R
	public static HBox getTimeBoxForTask(Task task) {
		TimeInterval timeInterval = task.getInterval();
		HBox overallTimeBox = new HBox();
		overallTimeBox.setAlignment(Pos.CENTER_RIGHT);
		overallTimeBox.setStyle(Constant.CSS_STYLE_OVERALL_TIME_BOX);
		if (timeInterval.getStartDate().equals(Constant.FLOATING_START_DATE)) {
			return null;
		} else if (timeInterval.getStartDate().equals(Constant.DEADLINE_START_DATE)) {
			
			HBox deadlineBox = LayoutManager.getDeadlineBox(timeInterval.getEndDate());
			deadlineBox.setPrefWidth(343);
			overallTimeBox.getChildren().addAll(deadlineBox);
			
			return overallTimeBox;
		} else {
			HBox startDateBox = LayoutManager.getTimeBox(timeInterval.getStartDate());
			HBox endDateBox = LayoutManager.getTimeBox(timeInterval.getEndDate());
			Label toLabel = new Label("  to  ");
			
			toLabel.setStyle(Constant.CSS_STYLE_TO_LABEL);
			
			overallTimeBox.getChildren().addAll(startDateBox, toLabel , endDateBox);
			return overallTimeBox;
		}
	}

	//@author A0119379R
	public static HBox getTimeBox(Date date) {
		
		HBox timeBox = new HBox();
		VBox dateBox = new VBox();
		timeBox.setPrefWidth(153);
		timeBox.setAlignment(Pos.CENTER);
		timeBox.setStyle(Constant.CSS_STYLE_TIME_BOX
				+ "");
		
		dateBox.setAlignment(Pos.CENTER);		
		Label dateLabel = new Label(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(date));
		dateLabel.setStyle("-fx-text-fill: white;");
		
		Label weekdayLabel = new Label(new SimpleDateFormat("EE", Locale.ENGLISH).format(date));
		weekdayLabel.setStyle(Constant.CSS_STYLE_WEEKDAY_LABEL);
		
		Label timeLabel = new Label(new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(date));
		timeLabel.setStyle(Constant.CSS_STYLE_TIME_LABEL);
		
		dateBox.getChildren().addAll(dateLabel, weekdayLabel);
		timeBox.getChildren().addAll(dateBox, timeLabel);
	
		return timeBox;
	}

	//@author A0119379R
	public static HBox getDeadlineBox(Date date) {
		HBox timeBox = new HBox();
		VBox dateBox = new VBox();
		
		timeBox.setAlignment(Pos.CENTER);
		timeBox.setStyle(Constant.CSS_STYLE_TIME_BOX);
		
		dateBox.setAlignment(Pos.CENTER);
		Label deadlineLabel = new Label("DEADLINE:      ");
		deadlineLabel.setStyle(Constant.CSS_STYLE_DEADLINE_LABEL);
		
		Label dateLabel = new Label(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(date));
		dateLabel.setStyle("-fx-text-fill: white;");
		
		Label weekdayLabel = new Label(new SimpleDateFormat("EE", Locale.ENGLISH).format(date));
		weekdayLabel.setStyle(Constant.CSS_STYLE_WEEKDAY_LABEL);
		
		Label timeLabel = new Label(new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(date));
		timeLabel.setStyle(Constant.CSS_STYLE_TIME_LABEL);
		
		dateBox.getChildren().addAll(dateLabel, weekdayLabel);
		timeBox.getChildren().addAll(deadlineLabel, dateBox, timeLabel);
	
		return timeBox;
	}

	//@author A0119379R
	public static int getRowCount(GridPane pane) {
	    int numRows = pane.getRowConstraints().size();
	    for (int i = 0; i < pane.getChildren().size(); i++) {
	        Node child = pane.getChildren().get(i);
	        if (child.isManaged()) {
	            int rowIndex = GridPane.getRowIndex(child);
	            int rowEnd = GridPane.getRowIndex(child);
	            numRows = Math.max(numRows, (rowEnd != GridPane.REMAINING? rowEnd : rowIndex) + 1);
	        }
	    }
	    return numRows;
	}

	//@author A0119447Y
	public static void setDisplayRow(GridPane pane, double height) {
		pane.getRowConstraints().add(new RowConstraints(height));
	}

	//@author A0119447Y
	public static Double calculateDescriptionWidth(Double fullWidth) {
		return fullWidth * 0.646 - 29;
	}
	
	//@author A0119447Y
	public static Double calculateTimeWidth(Double fullWidth) {
		return fullWidth * 0.37 - 29;
	}
}
