package org.hanns.physiology.statespace.ros.testnodes;

import ctu.nengoros.util.SL;

/**
 * The same as motivation Receiver, but here, message with no reward is sent each step 
 * (as each response to message received)
 * 
 * @author Jaroslav Vitku
 *
 */
public class MotivationReceiverNoReward extends MotivationReceiver{

	public static String name = "MotivationReceiverFullReward";

	/**
	 * Allow event-driven operation of the node?
	 * @param response false if the response should not be sent automatically
	 */
	public void setAutoResponse(boolean response){
		this.allowAutoResponse = response;
	}

	/**
	 * Called by the interrupt onNewMassage
	 * 
	 * @param data array of floats received from the basic Source motivation
	 */
	protected void onNewDataReceived(float[] data){
		System.out.println(step+++"new data "+SL.toStr(data));

		this.lastRecReward = data[0];
		this.lastRecMotivation = data[1];

		if(this.allowAutoResponse){	// if auto-response allowed, send default reward
			this.sendReward();
		}
	}
	
	@Override
	public void sendReward(){
		std_msgs.Float32MultiArray message = dataPublisher.newMessage();
		message.setData(new float[]{0});		// no reward
		dataPublisher.publish(message);
	}

}

