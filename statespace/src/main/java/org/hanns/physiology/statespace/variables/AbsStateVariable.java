package org.hanns.physiology.statespace.variables;

public abstract class AbsStateVariable implements StateVariable{

	public static final float DEF_LIMBO = 1;
	public static final float DEF_CRITICAL = 0;
	
	public static final double INIT_VAL = DEF_LIMBO;
	protected double myValue;
	
	protected int numDims;
	
	// reinforcement just received?
	protected boolean justReinforced= false;	
	
	public AbsStateVariable(int numDimensions){
		this.numDims = numDimensions;
		this.justReinforced = false;
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
	
	@Override
	public boolean justReinforced() { return this.justReinforced; }

	@Override
	public int getDimensions() { return this.numDims; }
	
	@Override
	public float getValue() { return (float)myValue; }
	
	@Override
	public void hardReset(boolean arg0) {
		this.softReset(arg0);
	}

	@Override
	public void softReset(boolean arg0) {
		this.justReinforced = false;
		this.myValue = DEF_LIMBO;
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
