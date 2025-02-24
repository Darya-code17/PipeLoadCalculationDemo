package com.study.pipesloadcalculation.service;

import com.study.pipesloadcalculation.model.Pipe;
import com.study.pipesloadcalculation.model.TruckTrailer;

public interface PackingStrategy {
	boolean fitInContour(Pipe outerPipe, Pipe newPipe); /// fit in PIPE
	boolean fitInContour(TruckTrailer truckTrailer, Pipe newPipe); /// fit in TRUCK
	
}
