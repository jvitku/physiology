package org.hanns.physiology.statespace.ros.testnodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.ros.node.ConnectedNode;

/**
 * The same as the {@link MotivationReceiver}, but this is supposed to be used
 * directly from the console as demo (e.g. by the shell script receiver).
 * 
 * @author Jaroslav Vitku
 */
public class MotivationReceiverConsole extends MotivationReceiver{

	@Override
	public void onStart(ConnectedNode connectedNode) {
		log = connectedNode.getLog();
		log.info(me+"started, parsing parameters");

		this.buildDataIO(connectedNode);

		BufferedReader br = new BufferedReader ( new InputStreamReader ( System.in ) ); 


		System.out.println("\n\nThe communication is in closed-loop and event-driven");

		while(true){
			try {
				System.out.println("\n\n\t-Press any key (at any time) to send reinforcement"
						+ "(otherwise the response is R=0)"
						+ "\n\t-Press 'q' and Enter to stop the loop\n\n");

				//System.in.read();
				String commandInput = br.readLine( ) ;
				if(commandInput.length()>0 && commandInput.charAt(0)=='q'){
					System.out.println("\t-Simulation paused, press any key to continue, "
							+ "ctrl+c to stop this node");
					// do not respond to messages from the MotivationSource (ROS node)
					this.setAutoResponse(false);	
				}else{
					// send the reinforcement and response to requests from the MotivationSource
					this.setAutoResponse(true);
					this.sendReward();
				}
				//this.sendReward();
			} catch (IOException e) { }
		}
	}
}
