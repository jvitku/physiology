package org.hanns.physiology.statespace.ros.testnodes;

import org.hanns.physiology.statespace.ros.AbsMotivationSource;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.network.node.AbstractHannsNode;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;
import ctu.nengoros.util.SL;

/**
 * Receives the motivation and reward by the {@link AbsMotivationSource}
 * 
 * @author Jaroslav Vitku
 *
 */
public class MotivationReceiver extends AbstractHannsNode{

	public static String name = "MotivationReceiver";

	public static final int DLP = 100; // default log period
	
	/**
	 * Node IO
	 */
	// receive motivation and its negative derivation (=reward)
	public static final String topicDataIn = AbsMotivationSource.topicDataOut;
	// publish reward occasionally
	public static final String topicDataOut = AbsMotivationSource.topicDataIn;
	
	//public static final String topicDataIn = io+"MotivationReward";
	// publish reward occasionally
	//public static final String topicDataOut  = io+"Reward";

	public int inputDims = 2;	// publishes motivation and reward

	private int step = 0;

	@Override
	public void onStart(ConnectedNode connectedNode) {
		
		this.buildDataIO(connectedNode);
		
		
		
	}
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(name); }

	@Override
	protected void buildConfigSubscribers(ConnectedNode connectedNode) {	}

	@Override
	protected void buildDataIO(ConnectedNode connectedNode) {
		dataPublisher = connectedNode.newPublisher(topicDataOut, std_msgs.Float32MultiArray._TYPE);

		Subscriber<std_msgs.Float32MultiArray> dataSub = 
				connectedNode.newSubscriber(topicDataIn, std_msgs.Float32MultiArray._TYPE);

		dataSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();

				if(data.length != inputDims)
					log.error(me+":"+topicDataIn+": Received state description has" +
							"unexpected length of"+data.length+"! Expected: "+topicDataIn);
				else{
					// here, the state description is decoded and one SARSA step executed
					if(step % logPeriod==0)
						myLog(me+"<-"+topicDataIn+" Received new reward data: "
								+SL.toStr(data));
					// implement this
					onNewDataReceived(data);
				}
			}
		});
	}

	/**
	 * Some processing here
	 * @param data array of floats received from the basic Source motivation
	 */
	protected void onNewDataReceived(float[] data){
		System.out.println("new data "+SL.toStr(data));
		
	}
	
	@Override
	public ProsperityObserver getProsperityObserver() { return null; }


	@Override
	protected void parseParameters(ConnectedNode arg0) { 
		this.logPeriod = DLP;
		this.step = 0;
	}

	@Override
	protected void publishProsperity() {}

	@Override
	protected void registerParameters() {}

}
