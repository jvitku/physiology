package org.hanns.physiology.statespace.ros.testnodes;

import java.io.IOException;

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

		System.out.println("\n\nThe communication is in closed-loop and event-driven");

		while(true){
			try {
				System.out.println("\n\nPress any key to send reinforcement, otherwise" +
						"the R=0\n\n");

				System.in.read();
				this.sendReward();
			} catch (IOException e) { }
		}
	}
}
