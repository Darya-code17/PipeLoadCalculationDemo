package com.study.pipeloadcalculation.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {
	public static double addValue(double d, double change){
		BigDecimal bigDecimal = new BigDecimal(d + change);
		bigDecimal = bigDecimal.setScale(5, RoundingMode.HALF_UP);
		return bigDecimal.doubleValue();
	}
}
