package org.hanns.physiology.statespace.variables.impl;

import org.hanns.physiology.statespace.variables.AbsStateVariable;

/**
 * This state variable lineary decays from limbo area (1)
 * towards the purgatory one (0) each step by the value of
 * the variable {@link #decay}.
 * 
 *  Note that all values on multidimensional input are 
 *  summed together. If the value received is the same or 
 *  higher than the {@link #THRESHOLD}, the variable 
 *  returns to the limbo area, nothing happens in other cases.
 * 
 * @author Jaroslav Vitku
 *
 */
public class LinearDecay extends AbsStateVariable{
	
	public static final double THRESHOLD = 1;

	public LinearDecay(int numDimensions){
		super(numDimensions);
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
	}

	@Override
	public void makeStep(float[] input) {
		this.justReinforced = false;

		if(!super.checkDimensions(input))
			return;

		double in = this.sum(input);
		if(in >= THRESHOLD){
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

}