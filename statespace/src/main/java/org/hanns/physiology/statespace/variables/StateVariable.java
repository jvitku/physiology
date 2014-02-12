package org.hanns.physiology.statespace.variables;

import ctu.nengoros.network.common.Resettable;

/**
 * Physiological state variable should be held near the limbo area.
 * In the limbo area, the variable does not produce any motivation. 
 * The farer from the limbo area, the bigger the motivation produced is.
 * The dynamics is computed in the method makeStep, which should be called each 
 * simulation step. 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface StateVariable extends Resettable{
	
	/**
	 * Dynamics of the variable is computed here.
	 * 
	 * @param input array of float values - input to the state variable of predefined dimensionality.
	 */
	public void makeStep(float[] input);
	
	/**
	 * Whether the reinforcement (movement of state variable
	 * towards limbo area) was made was received in the current step.
	 * 
	 * @return true if reinforcement received 
	 */
	public boolean justReinforced();
	
	/**
	 * Return the value of this variable. The value should
	 * be from the interval [0,1], where 0 is in the limbo area, and
	 * 1 is in the critical area.
	 * 
	 * @return value from the interval [0,1], where 1 critical state, 0 means limbo
	 */
	public float getValue();
	
	/**
	 * If the state space is in limbo area, produces motivation of value zero
	 * 
	 * @return true if the state space 
	 */
	public boolean isInLimbo();
	
	/**
	 * Get the distance from the nearest limbo area. 
	 * This value should be (normalized to) in the interval [0,1].
	 *  
	 * @return distance to the limbo area.
	 */
	public double getDistToLibo();
	
	/**
	 * If the state space variable value is in the critical state.
	 * 
	 * @return if true, agent should die from critical value of this variable
	 */
	public boolean isCritical();
	
	
	/**
	 * Dimensions of this state variable.
	 * 
	 * @return return number of dimensions expected on the input of {@link #makeStep(float[])} method.
	 */
	public int getDimensions();
	
	/**
	 * Set decay: how fast the variable should decay towards purgatory area
	 * @param decay interval [0,1] defining speed of decay
	 */
	public void setDecay(double decay);

	/**
	 * Return the speed of decay.
	 * 
	 * @return from interval [0,1] defining how fast the value of variable 
	 * goes to the purgatory area
	 */
	public double getDecay();
	
}
