package org.hanns.physiology.statespace.ros.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hanns.physiology.statespace.ros.BasicMotivation;
import org.hanns.physiology.statespace.ros.testnodes.MotivationReceiver;
import org.junit.Test;

import ctu.nengoros.RosRunner;
import ctu.nengoros.nodes.RosCommunicationTest;

public class BasicMotivationTest extends RosCommunicationTest{

			public static final String SRC =
					"org.hanns.physiology.statespace.ros.BasicMotivation";
			//public static final String REC ="org.hanns.rl.discrete.ros.testnodes.GridWorldNode";
			
			public static final String[] SRC_command = new String[]{
				SRC,"_noInputs:=1","_decay:=0.1"};
			
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
				
				RosRunner mapr = super.runNode(MAP);	// run the motivation receiver
				assertTrue(mapr.isRunning());

				// check class instances and get them
				assertTrue(mot.getNode() instanceof BasicMotivation);
				BasicMotivation mt = (BasicMotivation) mot.getNode();
				assertTrue(mapr.getNode() instanceof MotivationReceiver);
				MotivationReceiver map = (MotivationReceiver)mapr.getNode();
				
				// TODO actually test here IO
				sleep(5000);
				
				// stop nodes
				mot.stop();								
				mapr.stop();
				assertFalse(mot.isRunning());
				assertFalse(mapr.isRunning());
			}
			

}
