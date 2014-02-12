package org.hanns.physiology.statespace.observers;

import ctu.nengoros.network.node.observer.Observer;

/**
 * Observer used for observing discrete SARSA algorithms.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface StateSpaceObserver extends Observer{

	/**
	 * Should be called each step in order to observe changes in the data
	 */
	public void observe();

}
