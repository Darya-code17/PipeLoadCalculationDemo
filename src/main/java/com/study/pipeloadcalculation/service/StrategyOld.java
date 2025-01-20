package com.study.pipeloadcalculation.service;

import com.study.pipeloadcalculation.model.Pipe;
import com.study.pipeloadcalculation.model.TruckTrailer;
import com.study.pipeloadcalculation.util.GeometryUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.util.GeometricShapeFactory;

public class StrategyOld implements PackingStrategy { // todo rename
	
	private CalculationService.PackingData calcUtils;
	
	
	public StrategyOld(CalculationService.PackingData packingData) {
		this.calcUtils = packingData;
	}
	
	
	@Override
	public boolean fitInContour(Pipe outerPipe, Pipe newPipe) { /// fit in PIPE
//		previous method:
		// start coordinates: center bottom (according to gravity)
		Coordinate startCenter = new Coordinate(
				outerPipe.getCenter().x,
				outerPipe.getCenter().y + ((double) outerPipe.getDiameterInner() / 2));
		int r = 1;
		int maxRadius = outerPipe.getDiameterInner();
		return isFitByRadialMethod(startCenter, r, maxRadius, newPipe, calcUtils.getInnerPipeSpace(outerPipe));
	}
	
	
	@Override
	public boolean fitInContour(TruckTrailer truckTrailer, Pipe newPipe) { /// fit in TRUCK
		//		 previous method:
		// start coordinates in the thuck: most left and bottom (according to gravity)
		Coordinate startCenter = new Coordinate(0, calcUtils.getTruck().getHeight());
		int maxRadius = Math.max(calcUtils.getTruck().getWidth(), calcUtils.getTruck().getHeight());
		int r = 1;
		return isFitByRadialMethod(startCenter, r, maxRadius, newPipe, calcUtils.getInnerTruckSpace(truckTrailer)); // old method
		
	}
	
	
	
	// don't like this method. Needs slight change
	private boolean isFitByRadialMethod(Coordinate pivot, int currentRadius, int maxRadius, Pipe pipeToPlace, Geometry geometryToFit) {
		// method in which centers od the circles - are control points of n-gon
		// the n-gon increases (and the number of vertices increases accordingly for more precision)
		// todo: perhaps the order of n-gon points in needed to be different. For example, start on the right (where 0 degrees)
		
		boolean fit = false;
		
		GeometryFactory geometryFactory = new GeometryFactory();
		GeometricShapeFactory gShape = new GeometricShapeFactory(geometryFactory);
		gShape.setCentre(pivot);
		
		int checkRadius = 0; // serves debug purposes
		
		while ((currentRadius < maxRadius) & (!fit)) {
			gShape.setSize(currentRadius * 2); // size in diameter (for GeometricShapeFactory)
			gShape.setNumPoints(GeometryUtils.numOfVertices(currentRadius)); // 32 default
			Geometry polygon = gShape.createCircle();
			
			// place circle at every vertex of this n-gon to see if it fits
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
	
	
	
	
	
}
