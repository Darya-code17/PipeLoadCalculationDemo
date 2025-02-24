package com.study.pipesloadcalculation.service;

import com.study.pipesloadcalculation.model.Pipe;
import com.study.pipesloadcalculation.model.TruckTrailer;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoadedData {
	
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
	
	
	
	public void loadData(List<TruckTrailer> listOfTruckTrailers, List<Pipe> listOfPipes) {
		purchaseList = new ArrayList<>();
//		loadPipesInformation(); // quick test
		purchaseList.addAll(listOfPipes);
		
		// sort pipes
		Collections.sort(purchaseList);
		Collections.reverse(purchaseList);
		
		truck = new TruckTrailer();
//		loadTrucksInformation(listOfTruckTrailers); // quick debug
		if (!listOfTruckTrailers.isEmpty()) {
			truck.setWidth(listOfTruckTrailers.getFirst().getWidth());
			truck.setHeight(listOfTruckTrailers.getFirst().getHeight());
		}
	}
	
	
	
	private void loadPipesInformation() { // example
		// load pipe's size
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
	
	
	
	private void loadTrucksInformation() { // example
		// load truck-trailer size
		truck.setWidth(1024);
		truck.setHeight(512);
	}
	
	
	
}