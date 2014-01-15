package org.hanns.physiology.statespace.motivationSource.impl;

import org.hanns.physiology.statespace.motivationSource.AbsSource;
import org.hanns.physiology.statespace.transformations.Transformation;
import org.hanns.physiology.statespace.variables.StateVariable;

public class BasicSource extends AbsSource{

	public BasicSource(StateVariable var,Transformation t){
		super(var,t);
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
			return DEF_REWARD;
		else
			return 0;
	}
}
