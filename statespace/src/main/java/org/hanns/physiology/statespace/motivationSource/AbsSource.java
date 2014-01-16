package org.hanns.physiology.statespace.motivationSource;

import org.hanns.physiology.statespace.transformations.Transformation;
import org.hanns.physiology.statespace.variables.StateVariable;


public abstract class AbsSource implements Source{

	public static double DEF_MAX = 1;
	public static double DEF_MIN = 0;

	public static final float DEF_REWARD = 15;//TODO set 1
	
	protected StateVariable myVar;
	protected Transformation myT;

	protected int step;

	protected double myVal;

	public AbsSource(StateVariable var,Transformation t){

		this.myVar = var;
		this.myT = t;
		this.step = 0;
		this.myVal = DEF_MIN;

	}

	@Override
	public float getMotivation() { return (float)this.myVal; }

	@Override
	public StateVariable getVariable() { return this.myVar;	}

	@Override
	public void setStateVariable(StateVariable var) { this.myVar = var; }

	@Override
	public Transformation getTransformation() { return this.myT; }

	@Override
	public void setTransformation(Transformation t) { this.myT = t;	}

	@Override
	public void hardReset(boolean randomize) {
		this.softReset(randomize);
		this.myVar.hardReset(randomize);
	}

	@Override
	public void softReset(boolean randomize) {
		this.myVar.softReset(randomize);
		this.step = 0;
	}

	/**
	 * Check if the value of the state variable is from the 
	 * expected range.
	 * @param stateVal value of my state variable
	 * @return true if range is OK, print error and return false if not
	 */
	protected boolean checkRane(float stateVal){
		if(stateVal<DEF_MIN || stateVal>DEF_MAX){
			System.err.println("Motivation Source: ERROR:" +
					" value of my state variable is out of range" +
					", the value is "+stateVal);
			return false;
		}
		return true;
	}
}
