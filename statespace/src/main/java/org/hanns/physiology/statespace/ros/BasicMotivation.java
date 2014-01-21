package org.hanns.physiology.statespace.ros;

import org.hanns.physiology.statespace.motivationSource.impl.BasicSource;
import org.hanns.physiology.statespace.transformations.impl.Sigmoid;
import org.hanns.physiology.statespace.variables.impl.LinearDecay;


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
 *    vaelue (15 for now).
 *    
 * @author Jaroslav Vitku
 *
 */
public class BasicMotivation extends AbsMotivationSource {

	@Override
	public void initStructures() {
		this.t = new Sigmoid();
		this.var = new LinearDecay(this.inputDims,this.decay);
		this.source = new BasicSource(var,t);
	}

	@Override
	protected void onNewDataReceived(float[] data) {
		this.source.makeStep(data);

		float rew = this.source.getReinforcement();
		float mot = this.source.getMotivation();

		// publish the current reinforcement and motivation values
		std_msgs.Float32MultiArray fl = dataPublisher.newMessage();
		fl.setData(new float[]{rew,mot});
		System.out.println("step+ "+step+" publihing r="
				+rew+" and motivation="+mot);
		dataPublisher.publish(fl);
	}

	@Override
	protected boolean isReady() {
		return (dataPublisher !=null && source!=null 
				&& t!=null && var!=null && source !=null);
	}
}
