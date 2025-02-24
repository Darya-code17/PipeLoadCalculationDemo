package com.study.pipesloadcalculation.controller;

import com.study.pipesloadcalculation.model.Pipe;
import com.study.pipesloadcalculation.model.TruckTrailer;
import com.study.pipesloadcalculation.service.CalculationService;
import com.study.pipesloadcalculation.util.WarningLog;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;

import java.util.List;
import java.util.function.BiConsumer;

public class MyAppController {
	
	
	@FXML
	private TableView<Pipe> pipeTableView;
	
	@FXML
	private TableView<TruckTrailer> truckTrailerTableView;
	
	@FXML
	private Pane drawPane;
	
	@FXML
	private ListView<CalculationService.PackingData> listViewVariations;
	
	@FXML
	private Button btnAddTruckTrailer;
	
	@FXML
	private Button btnRemoveTruckTrailer;
	
	
	private final CalculationService calculationService;
	
	
	public MyAppController() {
		this.calculationService = new CalculationService();
	}
	
	
	
	@FXML
	private void initialize() {
		prepareTableInput();
		prepareDrawPane();
	}
	
	
	
	@FXML
	private void onButtonCalculate() {
		List<CalculationService.PackingData> packingDataVariations = calculate();
		if (packingDataVariations == null) {
			WarningLog.showWarning("input data is invalid");
		} else {
			getVariations(packingDataVariations);
		}
	}
	
	
	
	@FXML
	private void onButtonAddPipe() {
		pipeTableView.getItems().add(new Pipe(300, 290, 3000));
	}
	
	
	
	@FXML
	private void onButtonAddTruckTrailer() {
		truckTrailerTableView.getItems().add(new TruckTrailer(0, 0));
	}
	
	
	
	@FXML
	private void onButtonDeletePipe() {
		Pipe currentPipe = pipeTableView.getSelectionModel().getSelectedItem();
		pipeTableView.getItems().remove(currentPipe);
	}
	
	
	
	@FXML
	private void onButtonDeleteTruck() {
		TruckTrailer currentTruckTrailer = truckTrailerTableView.getSelectionModel().getSelectedItem();
		truckTrailerTableView.getItems().remove(currentTruckTrailer);
	}
	
	
	
	@FXML
	private void onButtonDeleteCurrentPacking() {
		CalculationService.PackingData packingData = listViewVariations.getSelectionModel().getSelectedItem();
		listViewVariations.getItems().remove(packingData);
	}
	
	
	
	@FXML
	private void onButtonFillWithExample() {
		pipeTableView.getItems().clear();
		pipeTableView.getItems().addAll(List.of(
				new Pipe(416, 400, 6000),
				new Pipe(250, 220, 6000),
				new Pipe(250, 220, 6000),
				new Pipe(110, 100, 6000),
				new Pipe(110, 100, 6000),
				new Pipe(110, 100, 6000),
				new Pipe(110, 100, 6000),
				new Pipe(110, 100, 6000),
				new Pipe(110, 100, 6000),
				new Pipe(110, 100, 6000),
				new Pipe(110, 100, 6000)
		));
		truckTrailerTableView.getItems().clear();
		truckTrailerTableView.getItems().add(new TruckTrailer(1024, 512));
	}
	
	
	
	@FXML
	private void onVariationClicked(MouseEvent event) {
//		if (event.getClickCount() == 2) { // todo double mouse click
		drawPane.getChildren().clear();
		drawResult(listViewVariations.getSelectionModel().getSelectedItem());
//		}
	}
	
	
	
	private void prepareDrawPane() {
		var sizeListener = new javafx.beans.value.ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
				alignDrawPane();
			}
		};
		drawPane.widthProperty().addListener(sizeListener);
		drawPane.heightProperty().addListener(sizeListener);
	}
	
	
	
	private void prepareTableInput() {
		StringConverter<Integer> integerStringConverter = newStringConverter(); // for positive
		
		/// truck trailer
		
		List<TruckTrailer> trucks = List.of(new TruckTrailer(1024, 512));
		truckTrailerTableView.getItems().setAll(trucks);
		
		TableColumn<TruckTrailer, Integer> columnWidth = new TableColumn<>("width, mm");
		columnWidth.setCellValueFactory(new PropertyValueFactory<>("width"));
		columnWidth.setCellFactory(TextFieldTableCell.forTableColumn(integerStringConverter));
		columnWidth.setOnEditCommit(createEditCommitHandler(TruckTrailer::setWidth));
		
		TableColumn<TruckTrailer, Integer> columnHeight = new TableColumn<>("height, mm");
		columnHeight.setCellValueFactory(new PropertyValueFactory<>("height"));
		columnHeight.setCellFactory(TextFieldTableCell.forTableColumn(integerStringConverter));
		columnHeight.setOnEditCommit(createEditCommitHandler(TruckTrailer::setHeight));
		
		
		truckTrailerTableView.getColumns().addAll(columnWidth, columnHeight);
		
		
		/// pipes
		
		List<Pipe> pipes = List.of(
				new Pipe(300, 280, 3000),
				new Pipe(300, 280, 3000));
		
		pipeTableView.getItems().setAll(pipes);
		
		TableColumn<Pipe, Integer> columnOuterDiameter = new TableColumn<>("outer diameter, mm");
		columnOuterDiameter.setCellValueFactory(new PropertyValueFactory<>("diameterOuter"));
		columnOuterDiameter.setCellFactory(TextFieldTableCell.forTableColumn(integerStringConverter));
		columnOuterDiameter.setOnEditCommit(createEditCommitHandler(Pipe::setDiameterOuter));
		
		TableColumn<Pipe, Integer> columnInnerDiameter = new TableColumn<>("inner diameter, mm");
		columnInnerDiameter.setCellValueFactory(new PropertyValueFactory<>("diameterInner"));
		columnInnerDiameter.setCellFactory(TextFieldTableCell.forTableColumn(integerStringConverter));
		columnInnerDiameter.setOnEditCommit(createEditCommitHandler(Pipe::setDiameterInner));
		
		TableColumn<Pipe, Integer> columnLength = new TableColumn<>("length, mm");
		columnLength.setCellValueFactory(new PropertyValueFactory<>("length"));
		columnLength.setCellFactory(TextFieldTableCell.forTableColumn(integerStringConverter));
		columnLength.setOnEditCommit(createEditCommitHandler(Pipe::setLength));
		
		pipeTableView.getColumns().addAll(columnOuterDiameter, columnInnerDiameter, columnLength);
		
		
		// todo (3) temporary unavailable until there will be multiple truck trailers
		btnAddTruckTrailer.setDisable(true);
		btnRemoveTruckTrailer.setDisable(true);
	}
	
	
	
	private StringConverter<Integer> newStringConverter() {
		return
//				StringConverter<Integer> stringConverter =
				new StringConverter<Integer>() {
					@Override
					public String toString(Integer object) {
						if (object == null) {
							return "";
						} else {
							return object.toString();
						}
					}
					
					
					@Override
					public Integer fromString(String string) {
						if (string.matches("\\d+")) {
							return Integer.parseInt(string);
						} else {
							// convert if possible
							try {
								double dValue = Double.parseDouble(string);
								return (int) (Math.round(dValue));
							} catch (NumberFormatException e) {
								return 0;
							}
						}
					}
				};
	}
	
	
	
	public static <TableFillable> EventHandler<TableColumn.CellEditEvent<TableFillable, Integer>> createEditCommitHandler(BiConsumer<TableFillable, Integer> setter) {
		return event -> {
			if (event.getNewValue() != null && event.getNewValue() >= 0) {
				setter.accept(event.getRowValue(), event.getNewValue());
			} else {
				WarningLog.showWarning("Value must be >= 0");
			}
			event.getTableView().refresh();
		};
	}
	
	
	
	private boolean transmitDataToService() {
		List<TruckTrailer> listOfTruckTrailers = truckTrailerTableView.getItems().stream().toList();
		List<Pipe> listOfPipes = pipeTableView.getItems().stream().toList();
		
		return calculationService.loadData(listOfTruckTrailers, listOfPipes);
	}
	
	
	
	private void getVariations(List<CalculationService.PackingData> returnedList) {
		ObservableList<CalculationService.PackingData> dataVariations = FXCollections.observableArrayList(returnedList);
		listViewVariations.setItems(dataVariations);
	}
	
	
	
	private List<CalculationService.PackingData> calculate() {
		if (transmitDataToService()) {
			return calculationService.formPackageVariations();
		} else {
			return null;
		}
	}
	
	
	
	private void drawResult(CalculationService.PackingData pd) {
		if (pd == null) {
			return;
		}
		addNodesWithStyle(calculationService.drawTruck(pd), "dynamic-truckTrailer"); // dynamic styles
		addNodesWithStyle(calculationService.drawFittedPipes(pd), "dynamic-pipeFitted");
//		addNodesWithStyle(calculationService.drawUnfittedPipes(pd), "dynamic-pipeUnfitted");
		
		alignDrawPane();
	}
	
	
	
	private void alignDrawPane() {
		// get an object to orient to
		Node boundatyNode = getBiggestGeometryNode();
		if (boundatyNode == null) return;
		
		// apply scale
		double scale = getScale(boundatyNode, drawPane);
		drawPane.setScaleX(scale);
		drawPane.setScaleY(scale);
		
		// modify position since scale's pivot is at center
		drawPane.setTranslateX(-((drawPane.getWidth() - (drawPane.getWidth() * scale)) * 0.5));
		drawPane.setTranslateY(-((drawPane.getHeight() - (drawPane.getHeight() * scale)) * 0.5));
	}
	
	
	
	private Node getBiggestGeometryNode() {
		// truck as the biggest object
		var selectedPackage = listViewVariations.getSelectionModel().getSelectedItem();
		var listOfTrucks = calculationService.drawTruck(selectedPackage); // truck of selected package variation
		if (listOfTrucks.isEmpty()) return null;
		return listOfTrucks.getFirst();
	}
	
	
	
	private double getScale(Node truckTrailer, Pane pane) {
		double paneWidth = pane.getWidth();
		double truckWidth = truckTrailer.getLayoutBounds().getWidth();
		double paneHeight = pane.getHeight();
		double truckHeight = truckTrailer.getLayoutBounds().getHeight();
		
		double scale = 1.0;
		// modify scale
		if (truckWidth > paneWidth) {
			double scaleHor = paneWidth / truckWidth;
			scale = (Math.min(scale, scaleHor));
		}
		if (truckHeight > paneHeight) {
			double scaleVert = paneHeight / truckHeight;
			scale = (Math.min(scale, scaleVert));
		}
		return scale;
	}
	
	
	
	private void addNodesWithStyle(List<Node> list, String style) {
		list.forEach(node -> {
			node.getStyleClass().add(style);
			drawPane.getChildren().add(node);
		});
	}
	
	
}