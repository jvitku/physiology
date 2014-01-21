package org.hanns.physiology.statespace.ros.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hanns.physiology.statespace.ros.BasicMotivation;
import org.hanns.physiology.statespace.ros.testnodes.MotivationReceiver;
import org.junit.Test;

import ctu.nengoros.RosRunner;
import ctu.nengoros.nodes.RosCommunicationTest;

/**
 * Test whether the components work also over the ROS  network.
 * 
 * @author Jaroslav Vitku
 */
public class BasicMotivationTest extends RosCommunicationTest{

	public static final double dec = BasicMotivation.DEF_DECAY; // set the decay
	
	public static final String SRC =
			"org.hanns.physiology.statespace.ros.BasicMotivation";
	//public static final String REC ="org.hanns.rl.discrete.ros.testnodes.GridWorldNode";

	public static final String[] SRC_command = new String[]{
		SRC,"_noInputs:=1","_decay:="+dec};

	public static final String MAP = 
			"org.hanns.physiology.statespace.ros.testnodes.MotivationReceiver";

	/**
	 * Run the basic motivation source, check instance
	 * and stop it..
	 */
	@Test
	public void runMapAndRL(){

		// launch nodes
		RosRunner mot = super.runNode(SRC_command); // run the motivation
		assertTrue(mot.isRunning());

		RosRunner mapr = super.runNode(MAP);		// run the motivation receiver
		assertTrue(mapr.isRunning());

		System.out.println("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
		
		
		// check class instances and get them
		assertTrue(mot.getNode() instanceof BasicMotivation);
		BasicMotivation mt = (BasicMotivation) mot.getNode();
		assertTrue(mapr.getNode() instanceof MotivationReceiver);
		MotivationReceiver map = (MotivationReceiver)mapr.getNode();
		
		map.awaitInited();
		mt.awaitInited();
		
		sleep(100); 	// TODO: this may be necessary still..
		
		System.out.println("Initializeing the simulation now");
		map.sendReward();	// initiate the communication

		// wait for decay to decay towards zero 
		int numSteps = (int) (2*(1/dec));
		this.waitForDecay(map, numSteps);
		
		// check whether no reward is received and whether the motivaiton is high
		System.out.println("Last motivation was "+map.lastRecMotivation+" and the reward: "+map.lastRecReward);
		assertTrue(map.lastRecMotivation > 0.99);
		assertTrue(map.lastRecReward ==0);

		// stop the simulation 
		map.setAutoResponse(false);
		map.sendReward();
		sleep(10);			// wait for response
		
		// check whether the motivation is small and that the reward was passed through
		assertTrue(map.lastRecMotivation < 0.1);
		System.out.println("Last motivation was "+map.lastRecMotivation+" and the reward: "+map.lastRecReward);
		assertTrue(map.lastRecReward >=1);
		
		// restart the simulation
		map.setAutoResponse(true);
		map.sendReward();
		
		this.waitForDecay(map, numSteps);
		System.out.println("Last motivation was "+map.lastRecMotivation+" and the reward: "+map.lastRecReward);
		assertTrue(map.lastRecMotivation > 0.99);
		assertTrue(map.lastRecReward ==0);
		
		// stop nodes
		mot.stop();								
		mapr.stop();
		assertFalse(mot.isRunning());
		assertFalse(mapr.isRunning());
	}
	
	/**
	 * Wait at least a given number of simulation steps 
	 * @param map node with motivaitonReceiver
	 * @param numSteps num steps to wait (messages exchanged)
	 */
	private void waitForDecay(MotivationReceiver map, int numSteps){
		
		int start = map.getStep();
		
		while(map.getStep() < numSteps+start){
			sleep(300);
			System.out.println("steps: "+map.getStep());
		}
		
	}
}
