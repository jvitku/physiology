package org.hanns.physiology.statespace.ros.testnodes;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;

import ctu.nengoros.network.node.AbstractHannsNode;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;

/**
 * Receives the motivation and reward by the {@link AbsMotivationSource}
 * 
 * @author Jaroslav Vitku
 *
 */
public class MotivationReceiver extends AbstractHannsNode{

	@Override
	public GraphName getDefaultNodeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void buildConfigSubscribers(ConnectedNode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void buildDataIO(ConnectedNode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ProsperityObserver getProsperityObserver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(ConnectedNode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void parseParameters(ConnectedNode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void publishProsperity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void registerParameters() {
		// TODO Auto-generated method stub
		
	}

}
