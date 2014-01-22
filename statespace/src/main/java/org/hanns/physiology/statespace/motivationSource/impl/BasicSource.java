package org.hanns.physiology.statespace.motivationSource.impl;

import org.hanns.physiology.statespace.motivationSource.AbsSource;
import org.hanns.physiology.statespace.transformations.Transformation;
import org.hanns.physiology.statespace.variables.StateVariable;

public class BasicSource extends AbsSource{

	private final float reward;
	
	public BasicSource(StateVariable var,Transformation t){
		super(var,t);
		this.reward = DEF_REWARD;
	}

	/**
	 * Constructor
	 * @param var state variable that is used by this motivation source 
	 * @param t transformation that is used to transform value of the state variable
	 * to the value of motivation  
	 * @param reward this source provides "derivation" of received reward, after receiving
	 * the reward, state variable is moved to the limbo area and the reward output
	 * is set to the value of {@link #reward}
	 */
	public BasicSource(StateVariable var,Transformation t, float reward){
		super(var,t);
		this.reward = reward;
	}

	@Override
	public void makeStep(float[] input) {
		if(input.length != myVar.getDimensions()){
			System.err.println("Source: ERROR: expected input" +
					"of length "+myVar.getDimensions()+" but found "+input.length);
			return;
		}
		myVar.makeStep(input);
		this.updateMyVal();
	}

	private void updateMyVal(){
		if(!super.checkRane(myVar.getValue()))
			return;

		double v = myVar.getValue();
		this.myVal = this.myT.transform(v);
	}

	@Override
	public float getReinforcement() {
		if(this.myVar.justReinforced())
			return reward;
		else
			return 0;
	}
}

