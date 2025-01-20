package com.study.pipeloadcalculation.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class TruckTrailer {
	
	private int width;
	private int height;
//	private int length; // todo (3) in later versions
	
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	
	public int getHeight() {
		return height;
	}
	
	
	public int getWidth() {
		return width;
	}

	
	public Geometry getTruckTrailerPolygon() {
		// truckTrailer convert to JTS format
		Coordinate[] truckCoordinates = new Coordinate[]{
				new Coordinate(0, 0),
				new Coordinate(0, getHeight()),
				new Coordinate(getWidth(), getHeight()),
				new Coordinate(getWidth(), 0),
				new Coordinate(0, 0)
		};
		GeometryFactory geometryFactory = new GeometryFactory();
		return geometryFactory.createPolygon(truckCoordinates);
	}
	
	
}
