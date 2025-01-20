package com.study.pipeloadcalculation.controller;

import com.study.pipeloadcalculation.service.CalculationService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

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
	
	
	
	
	private void loadData(){
		calculationService.loadData(); // todo Repository class ?
	}
	private void calculationsReset(){
		calculationService.calculationsReset();
	}
	private void calculationsMake(){
		calculationService.calculationsMake();
	}
	private void drawResult(){
		drawPane.getChildren().addAll(
				calculationService.nodesToDraw()
		);
		
	}
}