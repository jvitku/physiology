package org.hanns.physiology.statespace.observers;

import org.hanns.physiology.statespace.variables.StateVariable;

import ctu.nengoros.network.node.observer.stats.AbsProsperityObserver;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;

/**
 * Observe Means State Distance to optimal conditions (MSD) value.
 * 
 *   MSD is defined as follows: MSD = sum(dist_i)/step
 * 
 * @author Jaroslav Vitku
 */
public class MSD extends AbsProsperityObserver{

	public final String name = "MSD";
	public final String explanation = "Value from [0,1] telling the" +
			"Means State Distance to optimal conditions (limbo area) (MSD).";

	// dist = sum(dist_i)
	// msd = dist/step;
	public double dist;	// total distance to optimal conditions (added each step) 
	
	// each step, measure how far from the limbo area the parent is
	StateVariable parent;	
	
	public MSD(StateVariable parent){
		this.parent = parent;
		this.hardReset(false);
	}

	@Override
	public void observe(int prevAction, float reward, int[] currentState, int futureAction){
		step++;
		
		dist += parent.getDistToLibo();	// add to sup

		if(this.shouldVis  && step % visPeriod==0)
			System.out.println("MSD is: "+this.getProsperity());
	}

	@Override
	public float getProsperity() {
		// compute the MSD value
		return (float)dist/step;				
	}

	@Override
	public void softReset(boolean randomize) {
		super.softReset(randomize);
		
		this.dist = 0;	//should it be here?
	}
	
	@Override
	public void hardReset(boolean randomize) {
		super.hardReset(randomize);
		
		this.dist = 0;
	}

	@Override
	public ProsperityObserver[] getChilds() {
		return null;
	}
	
	@Override
	public String getName() { return name;	}

	@Override
	public String getDescription() { return explanation; }

}