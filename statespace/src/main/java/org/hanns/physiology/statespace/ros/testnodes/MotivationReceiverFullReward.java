package org.hanns.physiology.statespace.ros.testnodes;

/**
 * The same as motivation Receiver, but here, reward is sent each step
 * 
 * @author Jaroslav Vitku
 *
 */
public class MotivationReceiverFullReward extends MotivationReceiver{

	public static String name = "MotivationReceiverFullReward";

	/**
	 * Allow event-driven operation of the node?
	 * @param response false if the response should not be sent automatically
	 */
	@Override
	public void setAutoResponse(boolean response){
		this.allowAutoResponse = response;
	}

	/**
	 * Called by the interrupt onNewMassage
	 * 
	 * @param data array of floats received from the basic Source motivation
	 */
	@Override
	protected void onNewDataReceived(float[] data){
		step++;
		
		this.lastRecReward = data[0];
		this.lastRecMotivation = data[1];

		if(this.allowAutoResponse){	// if autoresponse allowed, send default reward
			this.sendReward();
		}
	}

}
