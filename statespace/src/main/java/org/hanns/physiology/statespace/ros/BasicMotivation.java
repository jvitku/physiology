package org.hanns.physiology.statespace.ros;

import java.util.LinkedList;

import org.hanns.physiology.statespace.motivationSource.impl.BasicSource;
import org.hanns.physiology.statespace.observers.StateSpaceObserver;
import org.hanns.physiology.statespace.observers.impl.ProsperityMSD;
import org.hanns.physiology.statespace.transformations.impl.Sigmoid;
import org.hanns.physiology.statespace.variables.impl.LinearDecay;
import org.ros.node.ConnectedNode;

import ctu.nengoros.network.node.observer.Observer;
import ctu.nengoros.util.SL;


/**
 * Basic motivation source is source of motivation which
 * uses discrete state variable with linear decay from 
 * the limbo area (1) towards the purgatory one (0). 
 * 
 *  The motivation is produced by applying sigmoid function to 
 *  a value of the state variable. So the maximum motivation
 *  (~1) is produced in the purgatory area.
 *  
 *    After receiving the reward (sum of inputs is equal or 
 *    higher than 1), the variable is set to the limbo area
 *    and the reinforcement output is set to predefined nonzero
 *    value (15 for now).
 *    
 * @author Jaroslav Vitku
 *
 */
public class BasicMotivation extends AbsMotivationSource {

	// value of reward that is published further after receiving a reward
	public static final float DEF_REWARD = BasicSource.DEF_REWARD;
	public static final String rewardConf = "rewardValue";
	public float rewardVal;

	// all values above this value are evaluated as receiving reward  
	public static final double DEF_REWTHRESHOLD = LinearDecay.DEF_THRESHOLD;
	public static final String rewardThrConf = "rewardThrValue";
	public double rewardThr;

	@Override
	protected void registerObservers() {
		observers = new LinkedList<Observer>();
		this.o = new ProsperityMSD(this.var);
		observers.add(o);
	}


	@Override
	public void initStructures() {
		this.t = new Sigmoid();
		this.var = new LinearDecay(this.inputDims,this.decay, this.rewardThr);
		this.source = new BasicSource(var,t,this.rewardVal);
	}

	@Override
	protected void onNewDataReceived(float[] data) {
		this.source.makeStep(data);
		
		for(int i=0; i<observers.size(); i++)
			((StateSpaceObserver)observers.get(i)).observe();

		float rew = this.source.getReinforcement();
		float mot = this.source.getMotivation();

		if(step%logPeriod==0)
			log.info(me+"sending: "+SL.toStr(new float[]{rew,mot}));

		// publish the current reinforcement and motivation values
		std_msgs.Float32MultiArray fl = dataPublisher.newMessage();
		fl.setData(new float[]{rew,mot});
		dataPublisher.publish(fl);
		
		// publish current value of the prosperity
		this.publishProsperity();
	}

	@Override
	public boolean isStarted() {
		return (dataPublisher !=null && source!=null 
				&& t!=null && var!=null && source !=null);
	}

	@Override
	protected void registerParameters() {
		super.registerParameters();

		paramList.addParam(rewardConf, ""+DEF_REWARD, "Node publishes value of reward derivation, this is value of reward published");
		paramList.addParam(rewardThrConf, ""+DEF_REWTHRESHOLD, "If the sum of rewards on inputs is bigger than this threshold, " +
				"it is evaluated as reward");
	}

	@Override
	protected void parseParameters(ConnectedNode connectedNode) {
		super.parseParameters(connectedNode);

		double reward = r.getMyDouble(rewardConf, DEF_REWARD);
		rewardVal = (float)reward;
		rewardThr = r.getMyDouble(rewardThrConf, DEF_REWTHRESHOLD);
	}

	@Override
	public float getProsperity() { return o.getProsperity(); }

	@Override
	public String listParams() { return this.paramList.listParams(); }

	@Override
	public void hardReset(boolean randomize) {
		this.step = 0;
		this.var.hardReset(randomize);
		this.source.hardReset(randomize);
		for(int i=0;i<observers.size(); i++)
			observers.get(i).hardReset(randomize);
	}

	@Override
	public void softReset(boolean randomize){
		this.step = 0;
		this.var.softReset(randomize);
		this.source.softReset(randomize);
		for(int i=0;i<observers.size(); i++)
			observers.get(i).softReset(randomize);
	}


}
