package com.study.pipesloadcalculation.service;

import com.study.pipesloadcalculation.model.Pipe;
import com.study.pipesloadcalculation.model.TruckTrailer;
import com.study.pipesloadcalculation.util.GeometryUtils;
import com.study.pipesloadcalculation.util.WarningLog;
import com.vividsolutions.jts.geom.Geometry;
import javafx.scene.Node;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

public class CalculationService {
	
	// todo (2) next: few calculations with different coefficient and then choose the best (it will be map)
	public static double c1 = 1.006; // coefficient(scale) for increasing polygon's offset (inscribed circles, circular method)
	public static int c2 = 6; // coefficient(multiply) for increasing number of vertices of offsetting polygon
	public static int c3 = 4; // coefficient(multiply) for increasing number of vertices of pipe (collision checking)
	//
	private LoadedData data;
	private PackingStrategy packingStrategy;
	private PackingData packingData;
	private List<PackingData> packings;
	
	
	
	public CalculationService() {
		data = new LoadedData();
		packings = new ArrayList<>(); // PackingData
	}
	
	
	
	public boolean loadData(List<TruckTrailer> listOfTruckTrailers, List<Pipe> listOfPipes) {
		WarningLog.clear();
		// todo (3) list of truckTrailers
		if ((!validatedPipes(listOfPipes)) | (!validatedTruckTrailers(listOfTruckTrailers))) {
			WarningLog.showWarning();
			return false;
		}
		
		data.loadData(listOfTruckTrailers, listOfPipes);
		return true;
	}
	
	
	
	private boolean validatedTruckTrailers(List<TruckTrailer> trucks) {
		if ((trucks == null) || (trucks.isEmpty())) {
			WarningLog.addMessage("information about truck trailer is empty");
			return false;
		}
		var truck = trucks.getFirst();
		if (truck == null) {
			WarningLog.addMessage("information about truck trailer is empty");
			return false;
		}
		boolean valid;
		valid = TruckTrailer.validate(truck); // todo (3) list of Trucks
		return valid;
	}
	
	
	
	private boolean validatedPipes(List<Pipe> list) {
		if (list.isEmpty()) {
			WarningLog.addMessage("list of pipes is empty");
			return false;
		}
		boolean valid = true;
		for (Pipe pipe : list) {
			if (!Pipe.validate(pipe)) {
				valid = false;
			}
		}
		return valid;
	}


//	public void calculationsReset() {
//		// reset calculations (nullifying telescoped links)
//		data.getPurchaseList().forEach(pipe -> pipe.setTelescopedId(null)); // deep copy

//		packingData.setRequestList(new ArrayList<>());
//		packingData.setPackedList(new ArrayList<>());
//		packingData.setUnpackedList(new ArrayList<>());
//		packingData.setTruck(data.getTruck());
//	}
	
	
	
	public List<PackingData> formPackageVariations() {
//		for (c1 = 1.000; c1 < 1.010; c1 = MathUtils.addValue(c1, 0.001)) {
//		printCoefficients();
		packingData = new PackingData(data.getPurchaseList(), data.getTruck(), c1, c2, c3);
		packingStrategy = new CombinedStrategy(packingData);
//		calculationsReset(); // no need anymore
		calculationsMake();
		if (!packingData.getPackedList().isEmpty()) {
			packings.add(packingData);
		}
//		}
		return packings;
	}
	
	
	
	public void calculationsMake() {
		List<Pipe> reqList = packingData.getRequestList();
		
		// todo (3) later: (this is rare but) if Pipe's length is smaller than truck's length - it's impossible to fit
		
		while (!reqList.isEmpty()) {
			Pipe nextPipe = reqList.getFirst();
			reqList.removeFirst();
			
			if ((placeInPipe(nextPipe)) || (placeInTruck(nextPipe))) {
				packingData.getPackedList().add(nextPipe);
			} else {
				nextPipe.resetCenter();
				packingData.getUnpackedList().add(nextPipe);
			}
		}
	}
	
	
	
	public List<Node> drawFittedPipes(PackingData packing) {
		return drawAllPipesFromList(packing.getPackedList());
	}
	
	
	
	public List<Node> drawUnfittedPipes(PackingData packing) {
		return drawAllPipesFromList(packing.getUnpackedList());
	}
	
	
	
	public List<Node> drawTruck(PackingData pd) {
		int index = packings.indexOf(pd);
		if (index == -1) {
			return new ArrayList<Node>();
		} else {
			return drawTruck(index);
		}
	}
	
	
	public List<Node> drawTruck(int numberOfPackingVariant) {
		List<Node> elements = new ArrayList<>();
		if (packings == null) return elements;
		if (packings.isEmpty()) return elements;
		try {
			TruckTrailer truckToDraw = packings.get(numberOfPackingVariant).getTruck();
			Polygon javafxTruckPolygon = GeometryUtils.javafxPolygon(truckToDraw.getTruckTrailerPolygon());
			elements.add(javafxTruckPolygon);
		} catch (IndexOutOfBoundsException e) {
			return elements;
		}
		return elements;
	}
	
	
	
	private void drawTextSummary() {
		// todo (5) later: this text will appear in UI, not in terminal
		// text summary
		System.out.println("\nFitted pipe in the truck, size " + data.getTruck().getWidth() + "  x " + data.getTruck().getHeight() + ":");
		packingData.getPackedList().forEach(System.out::println);
		
		System.out.println();
		
		System.out.println("Unable to fit:");
		packingData.getUnpackedList().forEach(System.out::println);
		
	}
	
	
	
	private List<Node> drawAllPipesFromList(ArrayList<Pipe> list) {
		List<Node> nodes = new ArrayList<>();
		for (Pipe pipe : list) {
			Polygon polygon = pipe.toJavaFXPolygon();
			nodes.add(polygon);
		}
		return nodes;
	}
	
	
	
	private boolean placeInPipe(Pipe newPipe) {// telescoping
		// this algorithm tries to place pipe first in the largest one, then smaller and smaller...
		// pipes are already ordered by size, so it can just take them sequentially
		boolean fitted = false;
		for (Pipe outerPipe : packingData.getPackedList()) {
			if (newPipe.getDiameterOuter() < outerPipe.getDiameterOuter()) {
//				System.out.println("Attempt fit pipe [" + newPipe + "]\n\tin the pipe [" + outerPipe + "]");
				fitted = fitInContour(outerPipe, newPipe);
				if (fitted) {
//					System.out.println("\t\t[V] fitted");
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
	
	
	
	private void printCoefficients() {
		System.out.printf("n-gon scale (correction of poly imprecision): [%.3f]" +
						  " n-gon increased vertices: [%d]" +
						  " n-gon (pipe representation) increased vertices: [%d]" +
						  "\n"
				, c1, c2, c3
		);
	}
	
	
	
	private void catchGeometryToDebug(Geometry g) {
		Polygon javafxPolygon = GeometryUtils.javafxPolygon(g);
		data.getDebugList().add(javafxPolygon);
	}
	
	
	
	private List<Node> drawDebug() {
		List<Node> elements = new ArrayList<>();
		data.getDebugList().forEach(javafxPolygon -> {
			elements.add(javafxPolygon);
		});
		return elements;
	}
	
	
	
	public class PackingData {
		
		private ArrayList<Pipe> requestList; // pipe in loading process
		private ArrayList<Pipe> packedList; // fitted in (truck or pipe) (telescoped or loaded into a truck)
		private ArrayList<Pipe> unpackedList; // what didn't fit
		private TruckTrailer truck; // todo (4) will be list of trucks
		
		private final double coef1;
		private final int coef2;
		private final int coef3;
		
		
		public PackingData(ArrayList<Pipe> purchaseList, TruckTrailer truckTrailer, double c1, int c2, int c3) {

//			this.requestList = new ArrayList<>(purchaseList); // deep copy
			this.requestList = new ArrayList<>();
			purchaseList.forEach(pipe -> this.requestList.add(// deep copy
					new Pipe(pipe.getDiameterOuter(), pipe.getDiameterInner(), pipe.getLength())
			));
			
			// todo (3) it will be list of trucks
			this.truck = new TruckTrailer(truckTrailer.getWidth(), truckTrailer.getHeight()); // deep copy
			
			this.packedList = new ArrayList<>();
			this.unpackedList = new ArrayList<>();
			
			this.coef1 = c1;
			this.coef2 = c2;
			this.coef3 = c3;
		}
		
		
		
		public ArrayList<Pipe> getRequestList() {
			return this.requestList;
		}
		
		
		
		public ArrayList<Pipe> getPackedList() {
			return this.packedList;
		}
		
		
		
		public ArrayList<Pipe> getUnpackedList() {
			return this.unpackedList;
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
			// returns list of pipes in truckTrailer // todo (3) next: ... into specified truck
			ArrayList<Pipe> pipes = new ArrayList<>();
			for (Pipe p : getPackedList()) {
				if (p.getTelescopedId() == null) { // todo (3) (next: list of trucks) id will be checked
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
		
		
		
		@Override
		public String toString() {
			return "loaded pipes: %d, didn't fit: %d".formatted(
					packedList.size(),
					unpackedList.size());
		}
		
	}
	
}
