package com.study.pipesloadcalculation.model;

import com.study.pipesloadcalculation.util.WarningLog;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class TruckTrailer implements TableFillable {
	
	private int width;
	private int height;
//	private int length; // todo (3) in later versions
	
	
	
	public TruckTrailer() {
		this(0,0);
	}
	
	
	public TruckTrailer(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public static boolean validate(TruckTrailer truckTrailer) {
		boolean valid = true;
		
		if (truckTrailer.getWidth() == 0) {
			WarningLog.addMessage("empty parameter for truck trailer (width)");
			valid = false;
		} else if (truckTrailer.getHeight() == 0) {
			WarningLog.addMessage("empty parameter for truck trailer (height)");
			valid = false;
		}
//		} else if (truck.getLength() == 0) // todo (3)
		
		return valid;
	}
	
	
	
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
	
	
	
	@Override
	public String toString() {
		return "TruckTrailer ("
			   + Integer.toHexString(System.identityHashCode(this))
			   + ") {" +
			   "width=" + width +
			   ", height=" + height +
			   '}';
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
