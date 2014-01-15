package org.hanns.physiology.statespace.transformations;

/**
 * Transformation is applied to the value of physiological state variable
 * in order to get the value of motivation. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface Transformation {
	
	public double transform(double stateVar);

}
