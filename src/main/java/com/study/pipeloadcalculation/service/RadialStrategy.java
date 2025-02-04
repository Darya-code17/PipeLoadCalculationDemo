package com.study.pipeloadcalculation.service;

import com.study.pipeloadcalculation.model.Pipe;
import com.study.pipeloadcalculation.model.TruckTrailer;
import com.study.pipeloadcalculation.util.GeometryUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.util.GeometricShapeFactory;

public class RadialStrategy implements PackingStrategy {
	
	private CalculationService.PackingData packingData;
	
	
	public RadialStrategy(CalculationService.PackingData packingData) {
		this.packingData = packingData;
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
		return isFitByRadialMethod(startCenter, r, maxRadius, newPipe, packingData.getInnerPipeSpace(outerPipe));
	}
	
	
	@Override
	public boolean fitInContour(TruckTrailer truckTrailer, Pipe newPipe) { /// fit in TRUCK
		//		 previous method:
		// start coordinates in the thuck: most left and bottom (according to gravity)
		Coordinate startCenter = new Coordinate(0, packingData.getTruck().getHeight());
		int maxRadius = Math.max(packingData.getTruck().getWidth(), packingData.getTruck().getHeight());
		int r = 1;
		return isFitByRadialMethod(startCenter, r, maxRadius, newPipe, packingData.getInnerTruckSpace(truckTrailer)); // old method
		
	}
	
	
	
	// don't like this method. Needs slight change
	private boolean isFitByRadialMethod(Coordinate pivot, int currentRadius, int maxRadius, Pipe pipeToPlace, Geometry geometryToFit) {
		// method in which centers of the circles - are control points of n-gon
		// the n-gon increases (and the number of vertices increases accordingly for more precision)
		
		boolean fit = false;
		
		GeometryFactory geometryFactory = new GeometryFactory();
		GeometricShapeFactory gShape = new GeometricShapeFactory(geometryFactory);
		gShape.setCentre(pivot);
		
		int checkRadius = 0; // serves debug purposes
		
		while ((currentRadius < maxRadius) & (!fit)) {
			gShape.setSize(currentRadius * 2); // size in diameter (for GeometricShapeFactory)
			gShape.setNumPoints(GeometryUtils.numOfVertices(currentRadius)); // 32 default
			Geometry polygon = gShape.createCircle();
			
			// perhaps the order of n-gon points in needed to be different.
//			var newOrder = polygon.getCoordinates(); // default order
			var newOrder = GeometryUtils.coordinatesOrderCircular(polygon, pivot, 180, true); // specific coordinate's order
			
			// place circle at every vertex of this n-gon to see if it fits
			for (Coordinate coord : newOrder) {
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
