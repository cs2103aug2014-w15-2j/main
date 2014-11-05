

package infrastructure;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import reference.TimeInterval;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import dataStructure.Task;

public class LayoutManager {

	//author A0119379R
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

	//author A0119447Y
	public static GridPane getTaskPane(ArrayList<Task> taskList, double width) {
		GridPane taskPane = new GridPane();
		int row = 0;
		int index = 1;
		taskPane.getColumnConstraints().add(new ColumnConstraints(width * 0.3 - 21));
		taskPane.getColumnConstraints().add(new ColumnConstraints(width * 0.7 - 21));
		if (taskList != null) {
			for (Task task : taskList) {
				String bannerColor = LayoutManager.getBannerColor(task);
				HBox priorityPane = LayoutManager.getPriorityPane(bannerColor, index, width);
				taskPane.add(priorityPane, 0, row, 2, 1);
				LayoutManager.setDisplayRow(taskPane, Constant.GRID_ROW_HEIGHT);
				row ++;
				
				
				GridPane contentPane = LayoutManager.getContentPane(task, width);
				int numberOfSubrows = LayoutManager.getRowCount(contentPane);
				
				taskPane.add(contentPane, 0, row, 1, numberOfSubrows);
				LayoutManager.setDisplayRow(taskPane, Constant.GRID_ROW_HEIGHT * numberOfSubrows + 11);
				row ++;
				
				GridPane emptyPane = LayoutManager.getEmptyPane(width);
				taskPane.add(emptyPane, 0, row, 2, 1);
				LayoutManager.setDisplayRow(taskPane, Constant.GRID_ROW_HEIGHT);
				row ++;
				index ++;
			}
		}
		return taskPane;
	}

	//author A0119379R
	public static GridPane getEmptyPane(double width) {
		GridPane emptyPane = new GridPane();
		emptyPane.setStyle(Constant.CSS_STYLE_EMPTY_PANE);
		emptyPane.setPrefWidth(width);
		return emptyPane;
	}

	//author A0119379R
	public static GridPane getContentPane(Task task, double width) {
		String bodyColor = LayoutManager.getBodyColor(task);
		GridPane contentPane = LayoutManager.getContentPane(bodyColor, width);
	
		int subRow = 0;
	
		
		Label description = LayoutManager.getDescriptionLabel(task, width);
		
		contentPane.add(description, 0, subRow, 2, 1);
		LayoutManager.setDisplayRow(contentPane, Constant.GRID_ROW_HEIGHT);
		subRow ++;
		
		HBox timeBox = LayoutManager.getTimeBoxForTask(task);
		if (timeBox != null) {
			contentPane.add(timeBox, 1, 0, 2, 2);
		}
		
		GridPane tagPane = getTagPaneForTask(task);
		contentPane.add(tagPane, 0, subRow, 2, 1);
		LayoutManager.setDisplayRow(contentPane, Constant.GRID_ROW_HEIGHT);
		subRow ++;
		return contentPane;
	}

	//author A0119447Y
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

	//author A0119447Y
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

	//author A0119447Y
	public static HBox getPriorityPane(String bannerColor, int index, double width) {
		HBox priorityPane = new HBox();
		priorityPane.setAlignment(Pos.CENTER_RIGHT);
		priorityPane.setStyle(String.format(Constant.CSS_STYLE_PRIORITY_PANE, bannerColor));
		priorityPane.setPrefWidth(width);
		
		Label priorityLabel = new Label(String.format("%d", index));
		priorityLabel.setStyle(Constant.CSS_STYLE_PRIORITY_LABEL);
		priorityLabel.setPrefWidth(width);
		priorityPane.getChildren().add(priorityLabel);
		return priorityPane;
	}

	//author A0119379R
	public static Label getDescriptionLabel(Task task, double width) {
		Label descriptionLabel = new Label(task.getDescription());
		descriptionLabel.setStyle(Constant.CSS_STYLE_DESCRIPTION_LABEL);
		descriptionLabel.setPrefWidth(width);
		return descriptionLabel;
	}

	//author A0119447Y
	public static GridPane getContentPane(String bodyColor, double width) {
		GridPane contentPane = new GridPane();
		contentPane.setGridLinesVisible(false);
	    contentPane.getColumnConstraints().add(new ColumnConstraints(width * 0.62 - 29));
		contentPane.getColumnConstraints().add(new ColumnConstraints(width * 0.38 - 29));
		contentPane.setStyle(String.format(Constant.CSS_STYLE_CONTENT_PANE, bodyColor));
		contentPane.setPrefWidth(width);
		return contentPane;
	}

	//author A0119379R
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

	//author A0119379R
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

	//author A0119379R
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

	//author A0119379R
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

	//author A0119447Y
	public static void setDisplayRow(GridPane pane, double height) {
		pane.getRowConstraints().add(new RowConstraints(height));
	}

}
