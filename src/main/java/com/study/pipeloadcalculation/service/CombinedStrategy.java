package com.study.pipeloadcalculation.service;

import com.study.pipeloadcalculation.model.Pipe;
import com.study.pipeloadcalculation.model.TruckTrailer;

public class CombinedStrategy implements PackingStrategy {
	
	private CalculationService.PackingData packingData;
	
	
	
	public CombinedStrategy(CalculationService.PackingData packingData) {
		this.packingData = packingData;
	}
	
	
	
	@Override
	public boolean fitInContour(Pipe outerPipe, Pipe newPipe) {
		return new StrategyNew(packingData).fitInContour(outerPipe, newPipe);
	}
	
	
	
	@Override
	public boolean fitInContour(TruckTrailer truckTrailer, Pipe newPipe) {
		if (!(new StrategyNew(packingData).fitInContour(truckTrailer, newPipe))) { // if failed to fit by this strategy
			return new StrategyOld(packingData).fitInContour(truckTrailer, newPipe); // then use another strategy
		} else {
			return true;
		}
	}
}
