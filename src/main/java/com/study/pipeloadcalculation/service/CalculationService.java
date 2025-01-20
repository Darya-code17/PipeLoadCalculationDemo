package com.study.pipeloadcalculation.service;

import com.study.pipeloadcalculation.model.Pipe;
import com.study.pipeloadcalculation.model.TruckTrailer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.*;

public class CalculationService {
	
	// todo (2) next: few calculations with different coefficient and then choose the best (it will be map)
	public static double k1 = 1.005; // coefficient(scale) for increasing polygon's offset (inscribed circles, circular method)
	public static int k2 = 5; // coefficient(multiply) for increasing number of vertices of offsetting polygon
	public static int k3 = 3; // coefficient(multiply) for increasing number of vertices of pipe (collision checking)
	//
	private LoadedData data;
	private PackingData packingData;
	private PackingStrategy packingStrategy;
	
	
	public CalculationService() {
		data = new LoadedData();
		packingData = new PackingData();
		packingStrategy = new StrategyNew(packingData); // todo (2) initialize at another place
	}
	
	
	public List<Node> nodesToDraw() {
		List<Node> elements = new ArrayList<>();
		elements.addAll(drawTruck());
		elements.addAll(drawPipes());
		elements.addAll(drawDebug());
		drawTextSummary();
		return elements;
	}
	
	
	
	public void loadData() {
		data.loadData();
	}
	
	
	
	private boolean validated(ArrayList<Pipe> list) {
		// todo validate all fields and !emptylist
		return true; // temporary
	}
	
	
	
	
	public void calculationsReset() {
		// reset calculations (nullifying telescoped links)
		data.getPurchaseList().forEach(pipe -> pipe.setTelescopedId(null));
		
		packingData.setRequestList(new ArrayList<>());
		packingData.setPackedList(new ArrayList<>());
		packingData.setUnpackedList(new ArrayList<>());
		packingData.setTruck(data.getTruck());
	}
	
	
	
	
	public void calculationsMake() {
		packingData.getRequestList().clear();
		packingData.getRequestList().addAll(data.getPurchaseList());
		if (!validated(packingData.getRequestList())) {
			return;
		}
		
		// todo (3) later: (this is rare but) if Pipe's length is smaller than truck's length - it's impossible to fit
		
		while (!packingData.getRequestList().isEmpty()) {
			Pipe nextPipe = packingData.getRequestList().getFirst();
			packingData.getRequestList().removeFirst();
			
			if ((placeInPipe(nextPipe)) || (placeInTruck(nextPipe))) { // || for checking the both conditions
				packingData.getPackedList().add(nextPipe);
			} else {
				nextPipe.resetCenter();
				packingData.getUnpackedList().add(nextPipe);
			}
			
			
		}
		
	}
	
	
	private List<Node> drawTruck() {
		List<Node> elements = new ArrayList<>();
		// to draw truck
		javafx.scene.shape.Polygon javafxTruckPolygon = new javafx.scene.shape.Polygon();
		for (Coordinate coordinate : packingData.truck.getTruckTrailerPolygon().getCoordinates()) {
			javafxTruckPolygon.getPoints().addAll(coordinate.x, coordinate.y);
		}
		
		javafxTruckPolygon.setFill(Color.LIGHTSTEELBLUE);
		javafxTruckPolygon.setStroke(Color.LIGHTSLATEGREY);
		elements.add(javafxTruckPolygon);
		return elements;
	}
	
	
	
	private  List<Node> drawPipes() {
		List<Node> elements = new ArrayList<>();
		// to draw pipes
		elements.addAll(
				drawAllPipesFromList(packingData.getPackedList(), Color.rgb(255, 128, 64, 0.75), Color.BLACK));
		elements.addAll(
				drawAllPipesFromList(packingData.getUnpackedList(), Color.HOTPINK, Color.BLACK));
		return elements;
	}
	
	
	private void drawTextSummary() {
		// todo (5) later: this text will appear in UI, not in terminal
		// text summary
		System.out.println("\nFitted pipe in the truck, size " + data.getTruck().getWidth() + "  х " + data.getTruck().getHeight() + ":");
		packingData.getPackedList().forEach(System.out::println);
		
		System.out.println();
		
		System.out.println("Unable to fit:");
		packingData.getUnpackedList().forEach(System.out::println);
		
	}
	
	
	
	private List<Node> drawAllPipesFromList(ArrayList<Pipe> list, Color colorFill, Color colorStroke) {
		List<Node> elements = new ArrayList<>();
		for (Pipe pipe : list) {
			Polygon polygon = pipe.toJavaFXPolygon();
			polygon.setFill(colorFill);
			polygon.setStroke(colorStroke);
			elements.add(polygon);
		}
		return elements;
	}
	
	
	private boolean placeInPipe(Pipe newPipe) {// telescoping
		// this algorithm tries to place pipe first in the largest one, then smaller and smaller...
		// pipes are already ordered by size, so it can just take them sequentially
		boolean fitted = false;
		for (Pipe outerPipe : packingData.getPackedList()) {
			if (newPipe.getDiameterOuter() < outerPipe.getDiameterOuter()) {
				System.out.println("Attempt fit pipe [" + newPipe + "]\n\tin the pipe [" + outerPipe + "]");
				fitted = fitInContour(outerPipe, newPipe);
				if (fitted) {
					System.out.println("\t\t[V] fitted");
					newPipe.setTelescopedId(outerPipe);
					break;
				}
			}
		}
		return fitted;
	}
	
	
	private boolean placeInTruck(Pipe newPipe) {
		return fitInContour(packingData.getTruck(), newPipe);
	}
	
	
	
	private boolean fitInContour(Pipe outerPipe, Pipe newPipe) { /// PIPE
		return packingStrategy.fitInContour(outerPipe, newPipe);
	}
	
	
	
	private boolean fitInContour(TruckTrailer truckTrailer, Pipe newPipe) { /// TRUCK
		return packingStrategy.fitInContour(truckTrailer, newPipe);
	}
	
	
	
	
	
	
	private void catchGeometryToDebug(Geometry g) {
		var javafxPolygon = getJavaFXPolygon(g);
		javafxPolygon.setFill(Color.rgb(204, 255, 0, 0.1));
		javafxPolygon.setStroke(Color.rgb(0, 50, 255, 0.75));
		data.getDebugList().add(javafxPolygon);
	}
	
	
	private List<Node> drawDebug() {
		List<Node> elements = new ArrayList<>();
		data.getDebugList().forEach(javafxPolygon -> {
			elements.add(javafxPolygon);
		});
		return elements;
	}
	
	
	private javafx.scene.shape.Polygon getJavaFXPolygon(Geometry g) {
		return getJavaFXPolygon(g.getCoordinates());
	}
	
	
	
	private javafx.scene.shape.Polygon getJavaFXPolygon(Coordinate[] coords) {
		javafx.scene.shape.Polygon javafxPolygon = new javafx.scene.shape.Polygon();
		for (Coordinate coordinate : coords) {
			javafxPolygon.getPoints().addAll(coordinate.x, coordinate.y);
		}
		return javafxPolygon;
	}
	
	
	
	
	
	class PackingData {
		
		private ArrayList<Pipe> requestList; // pipe in loading process
		private ArrayList<Pipe> packedList; // fitted in (truck or pipe) (telescoped or loaded into a truck)
		private ArrayList<Pipe> unpackedList; // what didn't fit
		private TruckTrailer truck; // todo (4) will be list of trucks
		
		
		public ArrayList<Pipe> getRequestList() {
			return this.requestList;
		}
		
		
		public ArrayList<Pipe> getPackedList() {
			return this.packedList;
		}
		
		
		public void setPackedList(ArrayList<Pipe> packedList) {
			this.packedList = packedList;
		}
		
		
		public void setRequestList(ArrayList<Pipe> requestList) {
			this.requestList = requestList;
		}
		
		
		public ArrayList<Pipe> getUnpackedList() {
			return this.unpackedList;
		}
		
		
		public void setUnpackedList(ArrayList<Pipe> unpackedList) {
			this.unpackedList = unpackedList;
		}
		
		
		public void setTruck(TruckTrailer truck) {
			this.truck = truck;
		}
		
		
		public TruckTrailer getTruck() {
			return truck;
		}
		
		
		public ArrayList<Pipe> getPipesOf(Pipe outerPipe) {
			// returns list of pipes telescoped into specified pipe
			ArrayList<Pipe> pipes = new ArrayList<>();
			for (Pipe p : getPackedList()) {
				if (p.getTelescopedId() == outerPipe) {
					pipes.add(p);
				}
			}
			return pipes;
		}
		
		
		
		
		public ArrayList<Pipe> getPipesOf(TruckTrailer truck) {
			// returns list of pipes in container // todo (3) next: ... into specified container
			ArrayList<Pipe> pipes = new ArrayList<>();
			for (Pipe p : getPackedList()) {
				if (p.getTelescopedId() == null) { // todo (3) (next: list of containers) id will be checked
					pipes.add(p);
				}
			}
			return pipes;
		}
		
		
		
		
		public Geometry getInnerPipeSpace(Pipe outerPipe) {
			ArrayList<Pipe> relevantList = new ArrayList<>(); // list of Pipes telescoped in this outer Pipe
			for (Pipe p : getPackedList()) {
				if (p.getTelescopedId() == outerPipe) {
					relevantList.add(p);
				}
			}
			Geometry contour = outerPipe.toJTSGeometryInnerSpace();
			for (Pipe innerPipe : relevantList) {
				contour = contour.difference(innerPipe.toJTSGeometryOuterSpace());
			}
			return contour;
		}
		
		
		
		public Geometry getInnerTruckSpace(TruckTrailer truckTrailer) {
			ArrayList<Pipe> relevantList = new ArrayList<>(); // list of pipes inside the truck
			for (Pipe p : getPackedList()) { // todo (4) later: there will be not only pipes but boxes
				if (p.getTelescopedId() == null) {
					relevantList.add(p);
				}
			}
			Geometry contour = truckTrailer.getTruckTrailerPolygon();
			for (Pipe p : relevantList) {
				contour = contour.difference(p.toJTSGeometryOuterSpace());
			}
			return contour;
		}
		
	}
	
}
