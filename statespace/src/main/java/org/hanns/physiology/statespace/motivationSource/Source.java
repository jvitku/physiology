package org.hanns.physiology.statespace.motivationSource;

import org.hanns.physiology.statespace.transformations.Transformation;
import org.hanns.physiology.statespace.variables.StateVariable;

import ctu.nengoros.network.common.Resettable;

/**
 * Each source has own state variable (which represents dynamics of
 * agents internal state) and own transformation, which transforms
 * value of state variable to the value of motivation. 
 * 
 *  This motivation is sent to the network. 
 *  
 *  Also, the Source has another output: the derivation of
 *  reward received. If the value is moved back to the limbo 
 *  area, the reinforcement was received and this is sent
 *  further to the network.  
 * 
 * @author Jaroslav Vitku
 *
 */
public interface Source extends Resettable {
	
	/**
	 * This method should be called each step, it does:
	 * -updates value of own state variable
	 * -computes transformation of the result
	 * -updates its value of motivation
	 * @param input array of float variables expected on the input
	 */
	public void makeStep(float[] input);
	
	/**
	 * Return the current value of motivation produced
	 * @return produced motivation to some behaviour
	 */
	public float getMotivation();
	
	/**
	 * THe second output of the motivation source, 
	 * if the {@link StateVariable} was moved into the limbo
	 * area, the reinforcement was received, this binary information
	 * is sent further to the newtork. 
	 * 
	 * @return default value of the reinforcement if just received.
	 */
	public float getReinforcement();
	
	public StateVariable getVariable();
	
	public void setStateVariable(StateVariable var);
	
	public Transformation getTransformation();
	
	public void setTransformation(Transformation t);
}
