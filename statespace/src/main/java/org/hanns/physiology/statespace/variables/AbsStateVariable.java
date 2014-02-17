package org.hanns.physiology.statespace.variables;

/**
 * 
 * This class expects that the {@link #DEF_LIMBO} is < {@link #DEF_CRITICAL}
 * and the value of state variable goes from the limbo towards the critical. 
 * 
 * The speed of decay is set by the {@link #setDecay(double)}. The value of
 * the decay is expected in range of 0,1, if the value is set outside of this 
 * interval, min/max is used instead.
 * 
 *  
 * @author Jaroslav Vitku
 *
 */
public abstract class AbsStateVariable implements StateVariable{

	public static final float MIN_DECAY = 0;
	public static final float MAX_DECAY = 1;

	public static final float DEF_LIMBO = 1;
	public static final float DEF_CRITICAL = 0;

	public static final double INIT_VAL = DEF_LIMBO;
	protected double myValue;

	// how many steps it takes to get to the critical area?
	public static final double DEF_DECAY = 0.01;

	protected int numDims;

	protected double decay;

	// reinforcement just received?
	protected boolean justReinforced= false;	

	public AbsStateVariable(int numDimensions){
		this.numDims = numDimensions;
		this.justReinforced = false;
		this.decay = DEF_DECAY;
		this.softReset(false);
	}

	protected boolean checkDimensions(float[] input){
		if(input.length != this.numDims){
			System.err.println("StateVariable: ERROR: unexpected "
					+" size of input, expected: "+this.numDims+" received" +
					+input.length);
			return false;
		}
		return true;
	}

	public void setDecay(double decay){
		if(decay > MAX_DECAY){
			System.err.println("StateVariable: WARNING: decay is expected"
					+ " from the range ["+MIN_DECAY+", "+MAX_DECAY+"], "
					+ "will use: "+MAX_DECAY);
			decay = MAX_DECAY;
		}else if(decay < MIN_DECAY){
			System.err.println("StateVariable: WARNING: decay is expected"
					+ " from the range ["+MIN_DECAY+", "+MAX_DECAY+"], "
					+ "will use: "+MIN_DECAY);
			decay = MIN_DECAY;
		}
		this.decay = decay;
	}

	public double getDecay(){ return this.decay; }

	@Override
	public boolean justReinforced() { return this.justReinforced; }

	@Override
	public int getDimensions() { return this.numDims; }

	@Override
	public float getValue() { return (float)myValue; }

	@Override
	public void hardReset(boolean randomize) {
		this.softReset(randomize);
	}

	@Override
	public void softReset(boolean randomize) {
		this.justReinforced = false;
		this.myValue = INIT_VAL;
	}

}
