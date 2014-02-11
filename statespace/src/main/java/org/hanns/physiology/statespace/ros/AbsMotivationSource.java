package org.hanns.physiology.statespace.ros;

import java.util.LinkedList;

import org.hanns.physiology.statespace.motivationSource.Source;
import org.hanns.physiology.statespace.transformations.Transformation;
import org.hanns.physiology.statespace.variables.StateVariable;
import org.hanns.physiology.statespace.variables.impl.LinearDecay;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.network.node.AbstractConfigurableHannsNode;
import ctu.nengoros.network.node.infrastructure.rosparam.impl.PrivateRosparam;
import ctu.nengoros.network.node.infrastructure.rosparam.manager.ParamList;
import ctu.nengoros.network.node.observer.Observer;
import ctu.nengoros.network.node.observer.stats.ProsperityObserver;
import ctu.nengoros.network.node.synchedStart.StartupManager;
import ctu.nengoros.util.SL;

public abstract class AbsMotivationSource extends AbstractConfigurableHannsNode{

	public static final String NAME = "LinearPhysVar";
	public static final String me = "["+NAME+"] ";

	/**
	 * Node IO
	 */
	public static final String topicDataOut = io+"MotivationReward";
	public static final String topicDataIn  = io+"Reward";

	/**
	 * Variable configuration
	 */
	// the higher decay speed, the faster the reward needs to be received
	public static final String decayConf = "decaySpeed";
	public static final String topicDecay = conf+decayConf;
	public static final double DEF_DECAY = LinearDecay.DEF_DECAY;
	protected double decay;

	// number of inputs
	public static final int DEF_NOINPUTS = 1;
	protected int inputDims;

	/**
	 * HannsNode stuff
	 */
	protected ProsperityObserver o;				// observes the prosperity of node TODO
	protected LinkedList<Observer> observers;	// logging & visualization TODO

	/**
	 * Algorithm utilities
	 */
	protected int step = 0;
	protected StateVariable var;	// state variable implementation 
	protected Source source;		// motivation source
	protected Transformation t;		// transforms var->motivation
	
	protected String fullName = NAME;
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(NAME); }

	@Override
	public void onStart(ConnectedNode connectedNode) {
		log = connectedNode.getLog();
		//logger = new SL(this.get)

		System.out.println(me+"started, parsing parameters");
		this.registerParameters();
		paramList.printParams();

		this.parseParameters(connectedNode);
		// this.registerObservers(); // TODO

		System.out.println(me+"Creating data structures.");
		this.initStructures();
		
		System.out.println(me+"initializing ROS Node IO");

		// this.buildProsperityPublisher(connectedNode); // TODO
		this.buildConfigSubscribers(connectedNode);
		this.buildDataIO(connectedNode);
		
		fullName = super.getFullName(connectedNode);
		System.out.println(me+"Node configured and ready now!");
		
	}

	@Override
	protected void buildConfigSubscribers(ConnectedNode connectedNode) {
		/**
		 * Decay
		 */
		Subscriber<std_msgs.Float32MultiArray> alphaSub = 
				connectedNode.newSubscriber(topicDecay, std_msgs.Float32MultiArray._TYPE);

		alphaSub.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				if(data.length != 1)
					log.error("Decay config: Received message has " +
							"unexpected length of"+data.length+"!");
				else{
					logParamChange("RECEIVED chage of value DECAY",
							var.getDecay(), data[0]);
					var.setDecay(data[0]);
				}
			}
		});
	}

	@Override
	protected void buildDataIO(ConnectedNode connectedNode){
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
						System.out.println(me+"<-"+topicDataIn+" Received new reward data: "
								+SL.toStr(data));
					// implement this
					onNewDataReceived(data);
				}
			}
		});
	}

	/**
	 * New ROS message arrived, process the data and respond
	 * 
	 * @param data array of floats defining reward data
	 */
	protected abstract void onNewDataReceived(float[] data);

	@Override
	public ProsperityObserver getProsperityObserver() {
		// TODO Auto-generated method stub
		System.err.println("ProsperityObserver is TODO");
		return null;
	}

	@Override
	protected void parseParameters(ConnectedNode connectedNode) {
		r = new PrivateRosparam(connectedNode);
		logToFile= r.getMyBoolean(logToFileConf, DEF_LTF);
		logPeriod = r.getMyInteger(logPeriodConf, DEF_LOGPERIOD);

		System.out.println(me+"parsing parameters");

		inputDims = r.getMyInteger(noInputsConf, DEF_NOINPUTS);
		decay = r.getMyDouble(decayConf, DEF_DECAY);
	}

	/**
	 * Should initialize the state variable, transformation and
	 * the motivation source.
	 */
	public abstract void initStructures();

	@Override
	public void publishProsperity() {
		System.err.println("Prosperity publishing is TODO");
		// TODO Auto-generated method stub
	}

	@Override
	protected void registerParameters() {
		paramList = new ParamList();
		paramList.addParam(noInputsConf, ""+DEF_NOINPUTS,"Num dimensions of input data, input data is summed " +
				"and the value is evaluated as a reinforcement.");

		paramList.addParam(logToFileConf, ""+DEF_LTF, "Enables logging into file");
		paramList.addParam(logPeriodConf, ""+DEF_LOGPERIOD, "How often to log? -1 means never");

		paramList.addParam(decayConf, ""+DEF_DECAY, "Speed of decay of state variable each simulation step.");
	}

	@Override
	public String getFullName() { return this.fullName; }

	@Override
	public StartupManager getStartupManager() { return this.startup; }
	
}
