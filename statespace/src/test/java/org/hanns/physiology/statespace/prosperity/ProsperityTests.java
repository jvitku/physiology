package org.hanns.physiology.statespace.prosperity;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.hanns.physiology.statespace.ros.BasicMotivation;
import org.hanns.physiology.statespace.ros.testnodes.MotivationReceiverFullReward;
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

	public static final String[] MAP_command = new String[]{MAP,"_logPeriod:="+log};

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
		this.waitForSteps(map, numSteps);

		// check whether no reward is received and whether the motivation is high
		System.out.println("Last motivation was "+map.lastRecMotivation+" and the reward: "+map.lastRecReward);
		assertTrue(map.lastRecMotivation < 0.1);
		assertTrue(map.lastRecReward >= 1);


		// stop the simulation
		map.setAutoResponse(false);
		/*
			map.sendReward();
			sleep(10);				// wait for response

			// check whether the motivation is small and that the reward was passed through
			assertTrue(map.lastRecMotivation < 0.1);
			System.out.println("Last motivation was "+map.lastRecMotivation+" and the reward: "+
			map.lastRecReward);
			assertTrue(map.lastRecReward >=1);
		 */
	}


	private void waitForSteps(MotivationReceiverFullReward map, int numSteps){
		int start = map.getStep();
		
		System.out.println("Current value of the prosperity is: "+)

		while(map.getStep() < numSteps+start){
			sleep(30);
			System.out.println("steps: "+map.getStep());
		}
	}
}
