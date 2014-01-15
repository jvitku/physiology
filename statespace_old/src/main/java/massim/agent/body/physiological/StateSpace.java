package massim.agent.body.physiological;

import java.util.ArrayList;

public interface StateSpace {

	// get the number of variables in the space
	public int size();
	
	public ArrayList<Motivation> getMotivations();
	
	public double getMaxMotivation(int no);
	
	// whether some motivation has been added
	public boolean variableAdded();
	public void discardVariableAdded();
	
	public String getDataPath();
	public String getExperimentName();
}
