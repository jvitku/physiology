package org.hanns.physiology.statespace.transformations.impl;

import org.hanns.physiology.statespace.transformations.Transformation;

/**
 * Modified sigmoid function which produces values from interval 
 * [0,1].  Input values are expected from the interval [0,1], where 0
 * is the critical value (or the most far from a limbo area).
 * 
 * So the {@link StateVariable}s are responsible for rescaling own value into
 * the inteval [0,1].
 * 
 * @author Jaroslav Vitku
 */
public class Sigmoid implements Transformation{
	
	// here are rough min/max input values to the sigmoid 
	private final double min = -6;
	private final double max = 6;
	
	public Sigmoid(){
	}
	
	@Override
	public double transform(double stateVar) {
		// flip it (now 1 means worst case)
		stateVar = 1-stateVar;	
		// re-scale to the interval [-6,6]
		double x = min+(-min+max)*stateVar;
		
		// apply sigmoid
		double out = 1.0/(1.0+Math.exp(-x));
		return (float)out;
	}

}
