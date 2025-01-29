package com.study.pipeloadcalculation.controller;

import com.study.pipeloadcalculation.service.CalculationService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.List;

public class MyAppController {
	
	@FXML
	private Label welcomeText; // todo deprecate later
	
	@FXML
	private Pane drawPane;
	
	private CalculationService calculationService;
	
	
	@FXML
	protected void onHelloButtonClick() { // todo deprecate later
		welcomeText.setText("Welcome to JavaFX Application!");
	}
	
	
	public MyAppController() {
		this.calculationService = new CalculationService();
	}
	
	
	
	@FXML
	private void initialize() {
		loadData();
		calculationsReset();
		calculationsMake();
		drawResult();
	}
	
	
	
	private void loadData() {
		calculationService.loadData(); // todo Repository class ?
	}
	
	
	private void calculationsReset() {
		calculationService.calculationsReset();
	}
	
	
	private void calculationsMake() {
		calculationService.calculationsMake();
	}
	
	
	
	private void drawResult() {
		addNodesWithStyle(calculationService.drawTruck(), "dynamic-truckTrailer"); // dynamic styles
		addNodesWithStyle(calculationService.drawFittedPipes(), "dynamic-pipeFitted");
		addNodesWithStyle(calculationService.drawUnfittedPipes(), "dynamic-pipeUnfitted");
	}
	
	
	private void addNodesWithStyle(List<Node> list, String style) {
		list.forEach(node -> {
			node.getStyleClass().add(style);
			drawPane.getChildren().add(node);
		});
	}
	
	
}