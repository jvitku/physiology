package org.hanns.physiology.statespace.observers.impl;

import org.hanns.physiology.statespace.observers.StateSpaceProsperityObserver;
import org.hanns.physiology.statespace.variables.StateVariable;

import ctu.nengoros.network.node.observer.stats.AbsProsperityObserver;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;

/**
 * Prosperity measure based on the Mean State Distance to optimal conditions (MSD)
 * the smaller mean distance, the higher prosperity.
 * 
 * @see MSD
 *  
 * @author Jaroslav Vitku
 */
public class ProsperityMSD extends AbsProsperityObserver implements StateSpaceProsperityObserver{

	public final String name = "ProsperityMSD";
	public final String explanation = "Prosperity measure based on the Mean" +
			"State Distance to optimal conditions (MSD)," +
			" the smaller mean distance, the higher prosperity.";

	private MSD msd;

	public ProsperityMSD(StateVariable parent){
		// each step, measure how far from the limbo area the parent is
		msd = new MSD(parent);
		this.hardReset(false);
	}

	@Override
	public void observe(){
		msd.observe();
		step++;

		if(this.shouldVis  && step % visPeriod==0)
			System.out.println("ProsperityMSD is: "+this.getProsperity()+", " +
					"MSD is: "+msd.getProsperity());
	}

	@Override
	public float getProsperity() {
		// inverse MSD: smaller MSD the better
		return 1-msd.getProsperity();				
	}

	@Override
	public void softReset(boolean randomize) {
		super.softReset(randomize);
		msd.softReset(randomize);
	}

	@Override
	public void hardReset(boolean randomize) {
		super.hardReset(randomize);
		msd.hardReset(randomize);
	}

	@Override
	public ProsperityObserver[] getChilds() {
		return new ProsperityObserver[]{msd};	// one child
	}

	@Override
	public String getName() { return name;	}

	@Override
	public String getDescription() { return explanation; }

}