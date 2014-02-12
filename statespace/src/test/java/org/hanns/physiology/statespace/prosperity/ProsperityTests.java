package org.hanns.physiology.statespace.prosperity;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.hanns.physiology.statespace.ros.BasicMotivation;
import org.hanns.physiology.statespace.ros.testnodes.MotivationReceiver;
import org.hanns.physiology.statespace.ros.testnodes.MotivationReceiverFullReward;
import org.hanns.physiology.statespace.ros.testnodes.MotivationReceiverNoReward;
import org.junit.Ignore;
import org.junit.Test;

import ctu.nengoros.RosRunner;
import ctu.nengoros.network.common.exceptions.StartupDelayException;
import ctu.nengoros.network.node.testsuit.RosCommunicationTest;

public class ProsperityTests extends RosCommunicationTest{

	public static int log = 50; 								// log period
	public static final double dec = BasicMotivation.DEF_DECAY; // set the decay

	public static final String SRC =
			"org.hanns.physiology.statespace.ros.BasicMotivation";
	//public static final String REC ="org.hanns.rl.discrete.ros.testnodes.GridWorldNode";

	public static final String[] SRC_command = new String[]{
		SRC,"_noInputs:=1","_decay:="+dec,"_logPeriod:="+log};

	public static final String MAP = 
			"org.hanns.physiology.statespace.ros.testnodes.MotivationReceiverFullReward";
	
	public static final String NOMAP = 
			"org.hanns.physiology.statespace.ros.testnodes.MotivationReceiverNoReward";
	public static final String[] NOMAP_command = new String[]{NOMAP,"_logPeriod:="+log};

	public static final String[] MAP_command = new String[]{MAP,"_logPeriod:="+log};

	/**
	 * Reward received each sim step
	 */
	@Ignore
	@Test
	public void maxProsperity(){

		// launch nodes
		RosRunner mot = super.runNode(SRC_command); // run the motivation
		assertTrue(mot.isRunning());

		RosRunner mapr = super.runNode(MAP_command);// run the motivation receiver
		assertTrue(mapr.isRunning());

		// check class instances and get them
		assertTrue(mot.getNode() instanceof BasicMotivation);
		BasicMotivation mt = (BasicMotivation) mot.getNode();
		assertTrue(mapr.getNode() instanceof MotivationReceiverFullReward);
		MotivationReceiverFullReward map = (MotivationReceiverFullReward)mapr.getNode();

		try {
			map.awaitStarted();
			mt.awaitStarted();
		} catch (StartupDelayException e) {
			System.out.println("waited too long, fail..");
			e.printStackTrace();
			fail();
		}

		sleep(300); 	// TODO: #ROScommInited

		System.out.println("Initializeing the simulation now");
		map.sendReward();	// initiate the communication

		// make some steps 
		int numSteps = 300;
		// prosperity should be still 1, because reward is received each step
		this.waitForSteps(map, mt,numSteps, 1);

		// check whether no reward is received and whether the motivation is high
		System.out.println("Last motivation was "+map.lastRecMotivation+" and the reward: "+map.lastRecReward);
		assertTrue(map.lastRecMotivation < 0.1);	// no motivation is produced (in limbo)
		assertTrue(map.lastRecReward >= 1);			// reward received each step (also the last one)
		assertTrue(mt.getProsperity()>=1);			// just check again
	}

	

	/**
	 * Reward not received at all
	 */
	@Test
	public void zeroProsperity(){

		// launch nodes
		RosRunner mot = super.runNode(SRC_command); // run the motivation
		assertTrue(mot.isRunning());

		RosRunner mapr = super.runNode(NOMAP_command);// run the motivation receiver
		assertTrue(mapr.isRunning());

		// check class instances and get them
		assertTrue(mot.getNode() instanceof BasicMotivation);
		BasicMotivation mt = (BasicMotivation) mot.getNode();
		assertTrue(mapr.getNode() instanceof MotivationReceiverNoReward);
		MotivationReceiverNoReward map = (MotivationReceiverNoReward)mapr.getNode();

		try {
			map.awaitStarted();
			mt.awaitStarted();
		} catch (StartupDelayException e) {
			System.out.println("waited too long, fail..");
			e.printStackTrace();
			fail();
		}

		sleep(300); 	// TODO: #ROScommInited

		System.out.println("Initializeing the simulation now");
		map.sendReward();	// initiate the communication

		// make some steps 
		int numSteps = 300;
		// prosperity should be still 0, because no reward is received at all
		this.prosperityDecreases(map, mt, numSteps);

		// check whether no reward is received and whether the motivation is high
		System.out.println("Last motivation was "+map.lastRecMotivation+" and the reward: "+map.lastRecReward);
		assertTrue(map.lastRecMotivation > 0.9);	// motivation is big (BasicMotivation node produces big motivation)  
		assertTrue(map.lastRecReward >= 0);			// reward not received (also not the last one)
		System.out.println("eeeeeeeeeeeeeee "+mt.getProsperity());
		assertTrue(mt.getProsperity()<=0.2);		// check small value of prosperity
	}


	/**
	 * Check that the prosperity of node goes towards zero or is very small
	 * @param map motivationReceiver (test node)
	 * @param mt BasicMotivation (tested node)
	 * @param numSteps num steps (exchanged messages) to run them
	 */
	public void prosperityDecreases(MotivationReceiver map, BasicMotivation mt, int numSteps){
		int start = map.getStep();
		
		float prevProsp = 1.1f;
		
		while(map.getStep() < numSteps+start){
			sleep(3);
			System.out.println("steps: "+map.getStep()+" prosp: "+mt.getProsperity());
			assertTrue((mt.getProsperity() < prevProsp)|| mt.getProsperity()<0.001);
		}
	}
	
	
	private void waitForSteps(MotivationReceiver map, BasicMotivation mt, int numSteps, float expectedProsp){
		int start = map.getStep();
		
		while(map.getStep() < numSteps+start){
			sleep(3);
			System.out.println("steps: "+map.getStep()+" prosp: "+mt.getProsperity());
			
			// check the prosperity
			assertTrue(mt.getProsperity() == expectedProsp);
		}
	}
}
