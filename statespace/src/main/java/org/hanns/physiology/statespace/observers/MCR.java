package org.hanns.physiology.statespace.observers;

import ctu.nengoros.network.node.observer.stats.AbsProsperityObserver;


/**
 * Mean Cumulative Reward (MCR) is computed as follows: MCR = sum(reward_i)/steps
 * and describes mean amount of reward received during the simulation. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class MCR extends AbsProsperityObserver{

	public final String name = "MCR";
	public final String explanation = "Value from [0,1] defining" +
			"describes mean amount of reward received during the simulation" +
			" (1=reward each step).";

	private int rewards = 0;

	@Override
	public void observe(int prevAction, float reward, int[] currentState,
			int futureAction) {

		rewards++;
	}

	@Override
	public float getProsperity() { return (float)(rewards/step); }

	@Override
	public void softReset(boolean randomize) {
		super.softReset(randomize);
		rewards=0;
	}

	@Override
	public void hardReset(boolean randomize) {
		super.hardReset(randomize);
		rewards=0;
	}

	@Override
	public String getName() { return name;	}

	@Override
	public String getDescription() { return explanation;	}
}
