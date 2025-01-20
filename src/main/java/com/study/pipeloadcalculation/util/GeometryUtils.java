package com.study.pipeloadcalculation.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.util.GeometricShapeFactory;

import java.util.Arrays;
import java.util.Comparator;

public class GeometryUtils {
	
	public static Geometry adaptivePolygon(Coordinate center, double radius, int dense) {
		// more precise n-gon : number of control points depends on radius
		GeometryFactory geometryFactory = new GeometryFactory();
		GeometricShapeFactory shapeFactory = new GeometricShapeFactory(geometryFactory);
		shapeFactory.setCentre(center);
		shapeFactory.setSize(radius * 2);
		shapeFactory.setNumPoints(numOfVertices(radius) * dense); // 32 default
		return shapeFactory.createCircle();
	}

	
	
	public static int numOfVertices(double radius){
		return (int) (0.092 * radius + 4); // x=0 y=4  x=1000 y=96
	}
	
	
	
	public static Coordinate[] coordinatesOrder(Geometry geometry) { // todo add arguments to be able to choose order
		// returns ordered array of coordinates : lowest and leftmost has priority
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
	
	
	
	
	
	
	
}
