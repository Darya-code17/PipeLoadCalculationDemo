package com.study.pipeloadcalculation.model;

import com.study.pipeloadcalculation.service.CalculationService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class Pipe implements Comparable<Pipe>, Telescopable {
	
	private Telescopable telescopedId;
	private int diameterOuter; // unit: mm
	private int diameterInner; // unit: mm
	private int length; // unit: mm
	private Coordinate center;
	
	
	public Pipe(int diameterOuter, int diameterInner, int length) {
		this.diameterInner = diameterInner;
		this.diameterOuter = diameterOuter;
		this.length = length;
		center = new Coordinate();
	}
	
	
	
	public int getDiameterOuter() {
		return diameterOuter;
	}
	
	
	public int getDiameterInner() {
		return diameterInner;
	}
	
	
	public Telescopable getTelescopedId() {
		return telescopedId;
	}
	
	
	public void setTelescopedId(Telescopable telescopedId) {
		this.telescopedId = telescopedId;
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
		return CalculationService.adaptivePolygon(center, radius, CalculationService.k3); // more precise n-gon
	}
	
	
	public javafx.scene.shape.Polygon toJavaFXPolygon() {
		javafx.scene.shape.Polygon javafxPolygon = new javafx.scene.shape.Polygon();
		for (Coordinate coordinate : toJTSGeometryPrecise().getCoordinates()) {
			javafxPolygon.getPoints().addAll(coordinate.x, coordinate.y);
		}
		return javafxPolygon;
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
