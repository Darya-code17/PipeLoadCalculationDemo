package com.study.pipeloadcalculation.service;

import com.study.pipeloadcalculation.model.Pipe;
import com.study.pipeloadcalculation.model.TruckTrailer;
import com.vividsolutions.jts.geom.Geometry;

public interface PackingStrategy {
	boolean fitInContour(Pipe outerPipe, Pipe newPipe); /// fit in PIPE
	boolean fitInContour(TruckTrailer truckTrailer, Pipe newPipe); /// fit in TRUCK
	
}
