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
	
	
	
	public static int numOfVertices(double radius) {
		return (int) (0.092 * radius + 4); // x=0 y=4  x=1000 y=96
	}
	
	
	
	public static Coordinate[] coordinatesOrderRectangular(Geometry geometry, boolean right, boolean bottom) {
		// returns ordered array of coordinates. Boolean arguments are for priorities
		return Arrays.stream((geometry.getCoordinates())).sorted(
				(new Comparator<Coordinate>() {
					@Override
					public int compare(Coordinate o1, Coordinate o2) {
						int result;
						result = Double.compare(o1.y, o2.y) * (bottom ? 1 : -1);
						if (result == 0) {
							result = Double.compare(o1.x, o2.x) * (right ? 1 : -1);
						}
						return result;
					}
				})
		).distinct().toList().reversed().toArray(new Coordinate[0]);
	}
	
	
	
	public static Coordinate[] coordinatesOrderCircular(Geometry geometry, Coordinate geometryCenter, int startDegrees, boolean clockwise) { // atan2
		Coordinate[] coordinatesArray = geometry.getCoordinates(); // immutable list
		var mutableList = new java.util.ArrayList<>(               // mutable list
				Arrays.stream(coordinatesArray).distinct().toList()
		);
		
		Comparator<Coordinate> coordinateComparator = Comparator.comparingDouble(
				coordinate -> {
					double x = coordinate.x - geometryCenter.x;
					double y = coordinate.y - geometryCenter.y;
					double angleInRadians = Math.atan2(y, x) + (startDegrees * Math.PI / 180);
					
					double angleInDegrees = Math.toDegrees(angleInRadians);
					if (y <= 0) {
						angleInDegrees = -angleInDegrees;
					} else if (y > 0) {
						angleInDegrees = 360 - angleInDegrees;
					}
					double order = angleInDegrees;
					while (order < 0) {
						order += 360;
					}
					order *= (clockwise ? -1 : +1);
					return order;
				}
		);
		mutableList.sort(coordinateComparator);

//		ArrayList<> -> Coordinate[]
		return mutableList.toArray(new Coordinate[0]);
	}
	
	
}
