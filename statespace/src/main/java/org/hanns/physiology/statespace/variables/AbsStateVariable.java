package org.hanns.physiology.statespace.variables;

public abstract class AbsStateVariable implements StateVariable{

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

	@Override
	public boolean isInLimbo() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isCritical() {
		// TODO Auto-generated method stub
		return false;
	}
}
