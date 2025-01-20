package com.study.pipeloadcalculation.service;

import com.study.pipeloadcalculation.model.Pipe;
import com.study.pipeloadcalculation.model.TruckTrailer;
import com.study.pipeloadcalculation.util.GeometryUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.util.Collections;

public class StrategyNew implements PackingStrategy {
	
	private CalculationService.PackingData calcUtils;
	
	
	public StrategyNew(CalculationService.PackingData packingData) {
		this.calcUtils = packingData;
	}
	
	
	
	@Override
	public boolean fitInContour(Pipe outerPipe, Pipe newPipe) { /// fit in PIPE
		return isFitByCircularMethod(newPipe, calcUtils.getInnerPipeSpace(outerPipe), outerPipe); // new method
	}
	
	
	@Override
	public boolean fitInContour(TruckTrailer truckTrailer, Pipe newPipe) { /// fit in TRUCK
		return isFitByCircularMethod(newPipe, calcUtils.getInnerTruckSpace(truckTrailer), calcUtils.getTruck()); // new method (inscribed circular)
	}
	
	
	private boolean isFitByCircularMethod(Pipe pipeToPlace, Geometry geometryToFit, Pipe outerPipe) { // place inside the Pipe
		
		var listOfInnerPipes = calcUtils.getPipesOf(outerPipe);
		
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
		
		var listOfInnerPipes = calcUtils.getPipesOf(truckTrailer);
		
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
		// n-gon (whose vertices are potential centers of the circles)
		Geometry polygon = GeometryUtils.adaptivePolygon(
				outerPipe.getCenter(),
				(outerPipe.getDiameterOuter() / 2 + innerPipe.getDiameterOuter() / 2 // radius of n-gon
				) * CalculationService.k1 // since pipe's JTS contour isn't perfectly round but edgy, collision calculations are not precise. So there is nessesery to make n-gon a little bigger
				, CalculationService.k2 // coefficient for more vertices of n-gon
		);

//		catchGeometryToDebug((polygon)); // debug
		
		// place innerPipe at every point
		GeometryFactory geometryFactory = new GeometryFactory();
		for (Coordinate c : GeometryUtils.coordinatesOrder(polygon)) {
			if (geometryToFit.contains(geometryFactory.createPoint(c))) { // consider a point only if it's within given geometry
				var testCircle = innerPipe.toJTSGeometryOuterSpace(c);
				if (geometryToFit.contains(testCircle)) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	
	
}
