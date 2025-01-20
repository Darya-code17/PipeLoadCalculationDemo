package com.study.pipeloadcalculation.service;

import com.study.pipeloadcalculation.model.Pipe;
import com.study.pipeloadcalculation.model.TruckTrailer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.Collections;

public class LoadedData { // todo rename or separate
	
	private static ArrayList<Pipe> purchaseList; // what needs to be loaded
	// todo (4) next: instead of object truck - List of trucks
	private TruckTrailer truck;
	
	
	private ArrayList<Polygon> debugList = new ArrayList<>(); // (debug) display geometry added to this list
	
	
	public ArrayList<Pipe> getPurchaseList() {
		return purchaseList;
	}
	
	
	public ArrayList<Polygon> getDebugList() {
		return debugList;
	}
	
	
	public TruckTrailer getTruck() {
		return truck;
	}
	
	
	
	public void loadData() {
		
		loadPipesInformation();
		
		// sort pipes
		Collections.sort(purchaseList);
		Collections.reverse(purchaseList);
		
		truck = new TruckTrailer();
		loadTrucksInformation();
	}
	
	
	
	private void loadPipesInformation() {
		// load pipe's size
		purchaseList = new ArrayList<>();
		purchaseList.add(new Pipe(416, 400, 6000));
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
	
	
	
}