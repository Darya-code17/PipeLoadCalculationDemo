package com.study.pipesloadcalculation.service;

import com.study.pipesloadcalculation.model.Pipe;
import com.study.pipesloadcalculation.model.TruckTrailer;

public class CombinedStrategy implements PackingStrategy {
	
	private CalculationService.PackingData packingData;
	
	
	
	public CombinedStrategy(CalculationService.PackingData packingData) {
		this.packingData = packingData;
	}
	
	
	
	@Override
	public boolean fitInContour(Pipe outerPipe, Pipe newPipe) {
		return new CircularStrategy(packingData).fitInContour(outerPipe, newPipe);
	}
	
	
	
	@Override
	public boolean fitInContour(TruckTrailer truckTrailer, Pipe newPipe) {
		if (!(new CircularStrategy(packingData).fitInContour(truckTrailer, newPipe))) { // if failed to fit by this strategy
			return new RadialStrategy(packingData).fitInContour(truckTrailer, newPipe); // then use another strategy
		} else {
			return true;
		}
	}
}
