package com.study.pipeloadcalculation.controller;

import com.study.pipeloadcalculation.service.CalculationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.List;

public class MyAppController {
	
	@FXML
	private Pane drawPane;
	
	@FXML
	private Label packingDetails;
	
	@FXML
	private ListView<CalculationService.PackingData> listViewVariations;
	
	private final CalculationService calculationService;
	
	
	public MyAppController() {
		this.calculationService = new CalculationService();
	}
	
	
	
	@FXML
	private void initialize() {
		loadData();
		getVariations(calculationsMake());
	}
	
	
	
	private void getVariations(List<CalculationService.PackingData> returnedList) {
		ObservableList<CalculationService.PackingData> dataVariations = FXCollections.observableArrayList(returnedList);
		listViewVariations.setItems(dataVariations);
	}
	
	
	
	@FXML
	private void onVariationClicked(MouseEvent event) {
//		if (event.getClickCount() == 2) { // todo double mouse click
			drawPane.getChildren().clear();
			drawResult(listViewVariations.getSelectionModel().getSelectedItem());
//		}
	}
	
	
	
	private void loadData() {
		calculationService.loadData(); // todo Repository class ?
	}
	
	
	
	private List<CalculationService.PackingData> calculationsMake() {
		return calculationService.calculationsMake();
	}
	
	
	
	private void drawResult(CalculationService.PackingData pd) {
		addNodesWithStyle(calculationService.drawTruck(), "dynamic-truckTrailer"); // dynamic styles
		addNodesWithStyle(calculationService.drawFittedPipes(pd), "dynamic-pipeFitted");
		addNodesWithStyle(calculationService.drawUnfittedPipes(pd), "dynamic-pipeUnfitted");
		packingDetails.setText(pd.toString());
	}
	
	
	
	
	
	private void addNodesWithStyle(List<Node> list, String style) {
		list.forEach(node -> {
			node.getStyleClass().add(style);
			drawPane.getChildren().add(node);
		});
	}
	
	
}