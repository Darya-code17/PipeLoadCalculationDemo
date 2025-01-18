package com.study.pipeloadcalculation.service;

import com.study.pipeloadcalculation.model.Pipe;
import com.study.pipeloadcalculation.model.TruckTrailer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.*;

public class CalculationService {
	
	// todo (2) next: few calculations with different coefficient and then choose the best (it will be map)
	public static double k1 = 1.005; // coefficient(scale) for increasing polygon's offset (inscribed circles, circular method)
	public static int k2 = 5; // coefficient(multiply) for increasing number of anchor points of offsetting polygon
	public static int k3 = 3; // coefficient(multiply) for increasing number of anchor points of pipe (collision checking)
	//
	
	private ArrayList<Pipe> purchaseList; // what needs to be loaded
	private ArrayList<Pipe> requestList; // pipe in loading process
	private ArrayList<Pipe> packedList; // fitted in (truck or pipe) (telescoped or loaded into a truck)
	private ArrayList<Pipe> unpackedList; // what didn't fit
	// todo (4) next: instead of object truck - List of trucks
	private TruckTrailer truck;
	
	//	private int truckTrailerWidth;
//	private int truckTrailerHeight;
	private Geometry truckTrailerPolygon;
	
	private ArrayList<Polygon> debugList = new ArrayList<>(); // (debug) display geometry added to this list
	
	
	public List<Node> nodesToDraw() {
		List<Node> elements = new ArrayList<>();
		elements.addAll(drawTruck());
		elements.addAll(drawPipes());
		elements.addAll(drawDebug());
		drawTextSummary();
		return elements;
	}
	
	
	
	private void truckTrailerJTS() {
		// truckTrailer convet to JTS format // todo probably place in separate method. Because user can change truck size at any execution time
		Coordinate[] truckCoordinates = new Coordinate[]{
				new Coordinate(0, 0),
				new Coordinate(0, truck.getHeight()),
				new Coordinate(truck.getWidth(), truck.getHeight()),
				new Coordinate(truck.getWidth(), 0),
				new Coordinate(0, 0)
		};
		GeometryFactory geometryFactory = new GeometryFactory();
		truckTrailerPolygon = geometryFactory.createPolygon(truckCoordinates);
		
	}
	
	
	public void loadData() {
		loadPipesInformation();
		
		// sort pipes
		Collections.sort(purchaseList);
		Collections.reverse(purchaseList);
		
		truck = new TruckTrailer();
		loadTrucksInformation();
	}
	
	
	private boolean validated(ArrayList<Pipe> list) {
		// todo validate all fields and !emptylist
		return true; // temporary
	}
	
	
	private void loadPipesInformation() {
		// load pipe's size
		purchaseList = new ArrayList<>();
		purchaseList.add(new Pipe(400 + 16, 400, 6000));
		purchaseList.add(new Pipe(250, 220, 6000));
		purchaseList.add(new Pipe(250, 220, 6000));
		purchaseList.add(new Pipe(110, 100, 6000));
		purchaseList.add(new Pipe(110, 100, 6000));
		purchaseList.add(new Pipe(110, 100, 6000));
		purchaseList.add(new Pipe(110, 100, 6000));
		purchaseList.add(new Pipe(110, 100, 6000));
		purchaseList.add(new Pipe(110, 100, 6000));
		purchaseList.add(new Pipe(110, 100, 6000));
		purchaseList.add(new Pipe(110, 100, 6000));
	}
	
	
	private void loadTrucksInformation() {
		// load truck-trailer size
		truck.setWidth(1024);
		truck.setHeight(512);
	}
	
	
	public void calculationsReset() {
		// reset calculations (nullifying telescoped links)
		purchaseList.forEach(pipe -> pipe.setTelescopedId(null));
		
		requestList = new ArrayList<>();
		packedList = new ArrayList<>();
		unpackedList = new ArrayList<>();
	}
	
	
	
	public void calculationsMake() {
		requestList.clear();
		requestList.addAll(purchaseList);
		if (!validated(requestList)) {
			return;
		}
		
		truckTrailerJTS(); // need truck size (in JTS format) at the very beginning
		
		// todo (3) later: (this is rare but) if Pipe's length is smaller than truck's length - it's impossible to fit
		
		while (!requestList.isEmpty()) {
			Pipe nextPipe = requestList.getFirst();
			requestList.removeFirst();
			
			if ((placeInPipe(nextPipe)) || (placeInTruck(nextPipe))) { // || for checking the both conditions
				packedList.add(nextPipe);
			} else {
				nextPipe.resetCenter();
				unpackedList.add(nextPipe);
			}
			
			
		}
		
	}
	
	
	private List<Node> drawTruck() {
		List<Node> elements = new ArrayList<>();
		// to draw truck
		javafx.scene.shape.Polygon javafxTruckPolygon = new javafx.scene.shape.Polygon();
		for (Coordinate coordinate : truckTrailerPolygon.getCoordinates()) {
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
				drawAllPipesFromList(packedList, Color.rgb(255, 128, 64, 0.75), Color.BLACK));
		elements.addAll(
				drawAllPipesFromList(unpackedList, Color.HOTPINK, Color.BLACK));
		return elements;
	}
	
	
	private void drawTextSummary() {
		// todo (5) later: this text will appear in UI, not in terminal
		// text summary
		System.out.println("\nFitted pipe in the truck, size " + truck.getWidth() + "  х " + truck.getHeight() + ":");
		packedList.forEach(System.out::println);
		
		System.out.println();
		
		System.out.println("Unable to fit:");
		unpackedList.forEach(System.out::println);
		
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
		for (Pipe outerPipe : packedList) {
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
		
		return fitInContour(getInnerTruckSpace(), newPipe);
	}
	
	
	private Geometry getInnerTruckSpace() {
		ArrayList<Pipe> relevantList = new ArrayList<>(); // list of pipes inside the truck
		for (Pipe p : packedList) { // todo (4) later: there will be not only pipes but boxes
			if (p.getTelescopedId() == null) {
				relevantList.add(p);
			}
		}
		Geometry contour = truckTrailerPolygon;
		for (Pipe p : relevantList) {
			contour = contour.difference(p.toJTSGeometryOuterSpace());
		}
		return contour;
	}
	
	
	private Geometry getPipeInnerSpace(Pipe pipe) {
		ArrayList<Pipe> relevantList = new ArrayList<>(); // list of Pipes telescoped in this outer Pipe
		for (Pipe p : packedList) {
			if (p.getTelescopedId() == pipe) {
				relevantList.add(p);
			}
		}
		Geometry contour = pipe.toJTSGeometryInnerSpace();
		for (Pipe innerPipe : relevantList) {
			contour = contour.difference(innerPipe.toJTSGeometryOuterSpace());
		}
		return contour;
	}
	
	
	private boolean fitInContour(Pipe outerPipe, Pipe newPipe) { /// PIPE

//		previous method:
		// start coordinates: center bottom (according to gravity)
//		Coordinate startCenter = new Coordinate(
//				outerPipe.getCenter().x,
//				outerPipe.getCenter().y + ((double) outerPipe.getDiameterInner() / 2));
//		int r = 1;
//		int maxRadius = outerPipe.getDiameterInner();
//		return isFitByPolygonMethod2(startCenter, r, maxRadius, newPipe, getPipeInnerSpace(outerPipe));
		
		return isFitByCircularMethod(newPipe, getPipeInnerSpace(outerPipe), outerPipe); // new method
	}
	
	
	
	private boolean fitInContour(Geometry geometryToFit, Pipe newPipe) { /// TRUCK
//		 previous method:
		// start coordinates in the thuck: most left and bottom (according to gravity)
//		Coordinate startCenter = new Coordinate(0, truck.getHeight());
//		int maxRadius = Math.max(truck.getWidth(), truck.getHeight());
//		int r = 1;
//		return isFitByPolygonMethod2(startCenter, r, maxRadius, newPipe, geometryToFit); // old method
		
		return isFitByCircularMethod(newPipe, geometryToFit, truck); // new method (inscribed circular)
	}
	
	
	
	private boolean isFitByCircularMethod(Pipe pipeToPlace, Geometry geometryToFit, Pipe outerPipe) { // place inside the Pipe
		
		var listOfInnerPipes = getPipesOf(outerPipe);
		
		if (listOfInnerPipes.isEmpty()) {
			// outer Pipe is empty inside
			// place at lowest point (centered)
			Coordinate newCenter = new Coordinate(
					outerPipe.getCenter().x,
					outerPipe.getCenter().y + outerPipe.getDiameterInner() / 2 - pipeToPlace.getDiameterOuter() / 2 - 1
			);
			var testCircle = pipeToPlace.toJTSGeometryOuterSpace(newCenter);
			if (geometryToFit.contains(testCircle)) {
				return true;
			} else {
				return false;
			}
		} else {
			// outer Pipe has some other inner Pipes
			Collections.sort(listOfInnerPipes);
			var result = false;
			for (Pipe oneOfPipes : listOfInnerPipes.reversed()) {
				result = circularRevolveInside(pipeToPlace, oneOfPipes, geometryToFit);
				if (result) {
					break;
				}
			}
			return result;
		}
	}
	
	
	private boolean isFitByCircularMethod(Pipe pipeToPlace, Geometry geometryToFit, TruckTrailer truckTrailer) {// place inside the truck
		
		var listOfInnerPipes = getPipesOf(truckTrailer);
		
		if (listOfInnerPipes.isEmpty()) {
			// the truck's container is empty
			// place as lower and lefter as possible
			Coordinate newCenter = new Coordinate(
					pipeToPlace.getDiameterOuter() / 2 + 0, // todo (3) (when there will be list of containers) truckTrailer get x-coord
					truckTrailer.getHeight() - pipeToPlace.getDiameterOuter() / 2
			);
			var testCircle = pipeToPlace.toJTSGeometryOuterSpace(newCenter);
			if (geometryToFit.contains(testCircle)) {
				return true;
			} else {
				return false;
			}
		} else {
			// truck's container already has some Pipes or Boxes inside
			Collections.sort(listOfInnerPipes);
			var result = false;
			for (Pipe oneOfPipes : listOfInnerPipes.reversed()) {
				result = circularRevolveInside(pipeToPlace, oneOfPipes, geometryToFit);
				if (result) {
					break;
				}
			}
			return result;
		}
	}
	
	
	
	private boolean circularRevolveInside(Pipe innerPipe, Pipe outerPipe, Geometry geometryToFit) {
		// n-gon (whose anchor points are potential centers of the circles)
		Geometry polygon = adaptivePolygon(
				outerPipe.getCenter(),
				(outerPipe.getDiameterOuter() / 2 + innerPipe.getDiameterOuter() / 2 // radius of n-gon
				) * k1 // since pipe's JTS contour isn't perfectly round but edgy, collision calculations are not precise. So there is nessesery to make n-gon a little bigger
				, k2 // coefficient for more anchor points of n-gon
		);

//		catchGeometryToDebug((polygon)); // debug
		
		// place innerPipe at every point
		GeometryFactory geometryFactory = new GeometryFactory();
		for (Coordinate c : coordinatesOrder(polygon)) {
			if (geometryToFit.contains(geometryFactory.createPoint(c))) { // consider a point only if it's within given geometry
				var testCircle = innerPipe.toJTSGeometryOuterSpace(c);
				if (geometryToFit.contains(testCircle)) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	public static Geometry adaptivePolygon(Coordinate center, double radius, int dense) {
		// more precise n-gon : number of control points depends on radius
		GeometryFactory geometryFactory = new GeometryFactory();
		GeometricShapeFactory shapeFactory = new GeometricShapeFactory(geometryFactory);
		shapeFactory.setCentre(center);
		shapeFactory.setSize(radius * 2);
		
		int points;
		points = (int) (0.092 * radius + 4); // x=0 y=4  x=1000 y=96
//			System.out.println(currentRadius + "  " + points); // radius vs anchors
		
		shapeFactory.setNumPoints(points * dense); // 32 default
		return shapeFactory.createCircle();
	}
	
	
	private Coordinate[] coordinatesOrder(Geometry geometry) {
		// returns ordered array of coordinates : lowest and lestmost has priority
		return Arrays.stream((geometry.getCoordinates())).sorted(
				(new Comparator<Coordinate>() {
					@Override
					public int compare(Coordinate o1, Coordinate o2) {
						int result;
						result = Double.compare(o1.y, o2.y);
						if (result == 0) {
							result = -Double.compare(o1.x, o2.x);
						}
						return result;
					}
				})
		).toList().reversed().toArray(new Coordinate[0]);
	}
	
	
	
	private ArrayList<Pipe> getPipesOf(Pipe outerPipe) {
		// returns list of pipes telescoped into specified pipe
		ArrayList<Pipe> pipes = new ArrayList<>();
		for (Pipe p : packedList) {
			if (p.getTelescopedId() == outerPipe) {
				pipes.add(p);
			}
		}
		return pipes;
	}
	
	
	private ArrayList<Pipe> getPipesOf(TruckTrailer truck) {
		// returns list of pipes in container // todo (3) next: ... into specified container
		ArrayList<Pipe> pipes = new ArrayList<>();
		for (Pipe p : packedList) {
			if (p.getTelescopedId() == null) { // todo (3) (next: list of containers) id will be checked
				pipes.add(p);
			}
		}
		return pipes;
	}
	
	
	
	// don't like this method. Needs slight change
	private boolean isFitByRadialMethod(Coordinate pivot, int currentRadius, int maxRadius, Pipe pipeToPlace, Geometry geometryToFit) {
		// method in which centers od the circles - are control points of n-gon
		// the n-gon increases (and the number of anchor points increases accordingly for more precision)
		// todo: perhaps the order of n-gon points in needed to be different. For example, start on the right (where 0 degrees)
		
		boolean fit = false;
		
		GeometryFactory geometryFactory = new GeometryFactory();
		GeometricShapeFactory gShape = new GeometricShapeFactory(geometryFactory);
		gShape.setCentre(pivot);
		
		int checkRadius = 0; // serves debug purposes
		
		while ((currentRadius < maxRadius) & (!fit)) {
			gShape.setSize(currentRadius * 2); // size in diameter (for GeometricShapeFactory)
			
			int points;
			points = (int) (0.092 * currentRadius + 4); // x=0 y=4  x=1000 y=96 // todo replace with new formula
//			System.out.println(currentRadius + "  " + points); // radius vs anchors
			
			gShape.setNumPoints(points); // 32 default
			Geometry polygon = gShape.createCircle();
			
			// place circle at every anchor point of this n-gon to see if it fits
			for (Coordinate coord : polygon.getCoordinates()) {
				if (geometryToFit.contains(geometryFactory.createPoint(coord))) { // only if new center is located within required geometry
					Geometry testCircle = pipeToPlace.toJTSGeometryOuterSpace(coord);
					
					if (geometryToFit.contains(testCircle)) {
						
						// debug
//						if (pipeToPlace.getDiameterInner() == 101) {
//							catchGeometryToDebug(geometryToFit);
//						}
//						if (currentRadius > 1000) {
//						catchGeometryToDebug(polygon);
//						}
						
						fit = true;
						break;
					}
				}
			}
			currentRadius++;
			checkRadius = currentRadius; // serves debug purposes
		}
//		System.out.println(checkRadius); // serves debug purposes
		return fit;
	}
	
	
	
	private void catchGeometryToDebug(Geometry g) {
		var javafxPolygon = getJavaFXPolygon(g);
		javafxPolygon.setFill(Color.rgb(204, 255, 0, 0.1));
		javafxPolygon.setStroke(Color.rgb(0, 50, 255, 0.75));
		debugList.add(javafxPolygon);
	}
	
	
	private List<Node> drawDebug() {
		List<Node> elements = new ArrayList<>();
		debugList.forEach(javafxPolygon -> {
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
	
}
