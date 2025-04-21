package com.study.pipesloadcalculation.util;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class WarningLog {
	
	private static final List<String> warnings = new ArrayList<>();
	
	
	
	public static void clear() {
		warnings.clear();
	}
	
	
	
	public static void addMessage(String messageText) {
		if (!warnings.contains(messageText)) {
			warnings.add(messageText);
		}
	}
	
	
	
	public static void showWarning(String warningText) {
		Dialog<String> dialog = new Dialog<>();
//		dialog.setTitle("title");
		
		Label label = new Label();
		label.setText(warningText);
		Font currentFont = label.getFont();
		label.setFont(new Font(currentFont.getName(), 18));
//		label.setTextFill(textColor);
		
		VBox vbox = new VBox(label);
		dialog.getDialogPane().setContent(vbox);
		
		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		
		dialog.showAndWait();
	}
	
	public static void showWarning() {
		if (warnings.isEmpty()) {
			return;
		}
		String message = String.join("\n", warnings);
		
		showWarning(message);
	}
	
	
}
