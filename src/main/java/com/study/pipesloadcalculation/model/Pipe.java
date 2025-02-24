package com.study.pipesloadcalculation.model;

import com.study.pipesloadcalculation.service.CalculationService;
import com.study.pipesloadcalculation.util.GeometryUtils;
import com.study.pipesloadcalculation.util.WarningLog;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import javafx.scene.shape.Polygon;

public class Pipe implements Comparable<Pipe>, Telescopable, TableFillable {
	
	private Telescopable telescopedId;
	private int diameterOuter; // unit: mm
	private int diameterInner; // unit: mm
	private int length; // unit: mm
	private Coordinate center;
	
	
	
	public Pipe(int diameterOuter, int diameterInner, int length) {
		this.diameterInner = diameterInner;
		this.diameterOuter = diameterOuter;
		this.length = length;
		this.center = new Coordinate();
	}
	
	
	
	public static boolean validate(Pipe pipe) {
		boolean valid = true;
		
		if (pipe.getDiameterOuter() == 0) {
			WarningLog.addMessage("empty parameter for pipe (outer diameter)");
			valid = false;
		} else if (pipe.getDiameterInner() == 0) {
			WarningLog.addMessage("empty parameter for pipe (inner diameter)");
			valid = false;
		} else if (pipe.getLength() == 0) {
			WarningLog.addMessage("empty parameter for pipe (pipe length)");
			valid = false;
		} else if (pipe.getDiameterInner() >= pipe.getDiameterOuter()) {
			WarningLog.addMessage("wrong value: outer diameter can't be smaller than inner diameter");
			valid = false;
		}
		
		return valid;
	}
	
	
	
	public int getDiameterOuter() {
		return diameterOuter;
	}
	
	
	public int getDiameterInner() {
		return diameterInner;
	}
	
	
	public int getLength() {
		return length;
	}
	
	
	public Telescopable getTelescopedId() {
		return telescopedId;
	}
	
	
	public void setTelescopedId(Telescopable telescopedId) {
		this.telescopedId = telescopedId;
	}
	
	
	public void setDiameterInner(int diameterInner) {
		this.diameterInner = diameterInner;
	}
	
	
	public void setDiameterOuter(int diameterOuter) {
		this.diameterOuter = diameterOuter;
	}
	
	
	public void setLength(int length) {
		this.length = length;
	}
	
	
	public Coordinate getCenter() {
		return center;
	}
	
	
	private void setCenter(Coordinate center) {
		this.center = center;
	}
	
	
	public void resetCenter() {
		this.center.setCoordinate(new Coordinate(0, 0));
	}
	
	
	
	public com.vividsolutions.jts.geom.Geometry toJTSGeometryOuterSpace(Coordinate coordinate) {
		// returns circle representing how much space does it take (outer radius) + passed coordinates are applied
		setCenter(coordinate);
		return toJTSGeometryOuterSpace();
	}
	
	
	public com.vividsolutions.jts.geom.Geometry toJTSGeometryOuterSpace() {
		// returns circle representing how much space does it take (outer radius)
		return jtsCircle(center, getDiameterOuter() / 2);
	}
	
	
	
	public com.vividsolutions.jts.geom.Geometry toJTSGeometryInnerSpace() {
		// returns circle representing the inner space of the pipe (inner radius)
		return jtsCircle(center, getDiameterInner() / 2);
	}
	
	
	
	public com.vividsolutions.jts.geom.Geometry toJTSGeometryPrecise() {
		// returns ring representing precise pipe's thickness (for visualisation)
		com.vividsolutions.jts.geom.Geometry circle1 = jtsCircle(center, getDiameterOuter() / 2);
		com.vividsolutions.jts.geom.Geometry circle2 = jtsCircle(center, getDiameterInner() / 2);
		return circle1.difference(circle2);
	}
	
	
	
	private Geometry jtsCircle(Coordinate center, int radius) {
//		return geometryFactory.createPoint(center).buffer(radius); // default n-gon
		return GeometryUtils.adaptivePolygon(center, radius, CalculationService.c3); // more precise n-gon
	}
	
	
	public Polygon toJavaFXPolygon() {
		return GeometryUtils.javafxPolygon(toJTSGeometryPrecise());
	}
	
	
	@Override
	public int compareTo(Pipe other) {
		return Integer.compare(this.getDiameterOuter(), other.getDiameterOuter());
	}
	
	
	
	@Override
	public String toString() {
		return "Pipe   d (%d | %d)    center(%d, %d)"
				.formatted(
						diameterOuter,
						diameterInner,
						(int) center.x,
						(int) center.y
				);
	}
}
