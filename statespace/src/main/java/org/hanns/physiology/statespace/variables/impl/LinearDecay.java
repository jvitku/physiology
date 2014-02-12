package org.hanns.physiology.statespace.variables.impl;

import org.hanns.physiology.statespace.variables.AbsStateVariable;

/**
 * This state variable linearly decays from limbo area (1)
 * towards the purgatory one (0) each step by the value of
 * the variable {@link #decay}.
 * 
 *  Note that all values on multidimensional input are 
 *  summed together. If the value received is the same or 
 *  higher than the {@link #DEF_THRESHOLD}, the variable 
 *  returns to the limbo area, nothing happens in other cases.
 * 
 * @author Jaroslav Vitku
 *
 */
public class LinearDecay extends AbsStateVariable{

	public static final double DEF_THRESHOLD = 0.9;

	private final double threshold;

	public LinearDecay(int numDimensions){
		super(numDimensions);
		this.threshold = DEF_THRESHOLD;
	}

	/**
	 * Set number of dimensions of the input and how fast the
	 * variable should decay from the limbo area (1) towards the
	 * purgatory area in (0). Decay is for one step.
	 * 
	 * @param numDimensions number of dimensions of input data
	 * @param decayStep how much to decay for each simulation step
	 */
	public LinearDecay(int numDimensions, double decayStep){
		super(numDimensions);
		this.decay = decayStep;
		this.threshold = DEF_THRESHOLD;
	}

	/**
	 * Set number of dimensions of the input and how fast the
	 * variable should decay from the limbo area (1) towards the
	 * purgatory area in (0). Decay is for one step.
	 * 
	 * @param numDimensions number of dimensions of input data
	 * @param decayStep how much to decay for each simulation step
	 * @param threshold - defines minimum value on input that is evaluated 
	 * as a reward. If the received value is equal or bigger than threshold
	 * the state is moved to the limbo area.
	 */
	public LinearDecay(int numDimensions, double decayStep, double threshold){
		super(numDimensions);
		this.decay = decayStep;
		this.threshold = threshold;
	}

	@Override
	public void makeStep(float[] input) {
		this.justReinforced = false;

		if(!super.checkDimensions(input))
			return;

		double in = this.sum(input);
		if(in >= this.threshold){
			this.myValue = DEF_LIMBO;
			this.justReinforced = true;
		}else{
			this.myValue  = this.myValue - this.decay;
			if(this.myValue < DEF_CRITICAL)
				this.myValue = DEF_CRITICAL;
		}
	}

	protected double sum(float[] input){
		double out = 0;
		for(int i=0; i<input.length; i++){
			out = out +input[i];
		}
		return out;
	}

	@Override
	public double getDistToLibo() {
		return DEF_LIMBO - this.myValue;
	}

	@Override
	public boolean isInLimbo() { return this.myValue>=DEF_LIMBO; }

	@Override
	public boolean isCritical() { return this.myValue <=DEF_CRITICAL; }

}
