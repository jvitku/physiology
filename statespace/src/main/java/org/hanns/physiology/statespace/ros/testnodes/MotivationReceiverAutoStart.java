package org.hanns.physiology.statespace.ros.testnodes;

import org.ros.concurrent.CancellableLoop;
import org.ros.node.ConnectedNode;

public class MotivationReceiverAutoStart extends MotivationReceiverFullReward{

	protected boolean dataExchanged = false;
	
	@Override
	public void onStart(ConnectedNode connectedNode){
		super.onStart(connectedNode);
		
		this.waitForConnections(connectedNode);
	}
	
	/**
	 * This method is used for waiting for receiving communication.
	 * The node publishes current state of the environment, if in the 
	 * last second no message with action received. Newly connected agents 
	 * will respond with their action to this message.  
	 * 
	 * Note: it runs during entire simulation, so the waiting is renewed.
	 * 
	 * @param connectedNode
	 */
	protected void waitForConnections(ConnectedNode connectedNode){
		connectedNode.executeCancellableLoop(new CancellableLoop() {
			@Override
			protected void setup() {}
			@Override
			protected void loop() throws InterruptedException {
				Thread.sleep(1000);
				if(!dataExchanged){
					log.info(me+"No incomming data detected, publishing new reward message x");
					sendReward();
				}
				dataExchanged = false;
			}
		});
	}
}
