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
 * Receives the motivation and reward from the {@link AbsMotivationSource}
 * 
 * After pressing the enter, the non-zero reward is published
 * -after receiving data (reward,motivation) the R=0 is responded 
 * -after each pressing the enter, the non-zero reward is published, which returns
 * the motivation back towards zero
 * 
 * @author Jaroslav Vitku
 *
 */
public class MotivationReceiver extends AbstractHannsNode{

	public final int sleeptime = 10;
	public final int maxwait = 10000;

	public static String name = "MotivationReceiver";
	public static final String me = "["+name+"] ";

	public static final int DLP = 100; // default log period

	// default reinforcement to be sent after pressing thee nter
	public static final int DEFR = 2;

	public float lastRecMotivation = -1;
	public float lastRecReward = -1;

	private volatile boolean allowAutoResponse = true;	// event-driven opearation?

	/**
	 * Node IO
	 */
	// receive motivation and its negative derivation (=reward)
	public static final String topicDataIn = AbsMotivationSource.topicDataOut;
	// publish reward occasionally
	public static final String topicDataOut = AbsMotivationSource.topicDataIn;

	public int inputDims = 2;	// publishes motivation and reward
	private int step = 0;

	@Override
	public void onStart(ConnectedNode connectedNode) {
		log = connectedNode.getLog();
		log.info(me+"started, parsing parameters");

		this.buildDataIO(connectedNode);

		super.fullName = connectedNode.getResolver().getNamespace()+s+name;
		
		System.out.println("\n\nNode initialized. Use methods sendRward(), getStep() etc..");
	}

	/**
	 * Allow event-driven operation of the node?
	 * @param response false if the response should not be sent automatically
	 */
	public void setAutoResponse(boolean response){
		this.allowAutoResponse = response;
	}

	/**
	 * Current "simulation step"
	 * 
	 * @return basically the number of processed (correctly formatted) messages 
	 */
	public int getStep(){ return this.step; }

	/**
	 * publishes reward over the ROS network to a predefined topic
	 */
	public void sendReward(){
		std_msgs.Float32MultiArray message = dataPublisher.newMessage();
		message.setData(new float[]{DEFR});
		dataPublisher.publish(message);
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
		System.out.println(step+++"new data "+SL.toStr(data));

		this.lastRecReward = data[0];
		this.lastRecMotivation = data[1];

		if(this.allowAutoResponse){
			std_msgs.Float32MultiArray message = dataPublisher.newMessage();
			message.setData(new float[]{0});
			dataPublisher.publish(message);
		}
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

	@Override
	public boolean isReady(){
		return (log!=null && dataPublisher!=null);
	}

	@Override
	public float getProsperity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String listParams() {
		// TODO Auto-generated method stub
		return null;
	}


}
