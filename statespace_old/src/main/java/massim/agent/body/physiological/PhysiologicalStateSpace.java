package massim.agent.body.physiological;

//import massim.framework.util.xml.test.Loadable;

public interface PhysiologicalStateSpace extends /*Loadable,*/ StateSpace{

	// get the length of array containing variables in the physiological state sp.
	public int size();
	
	// handle given variable
	public double getVal(int no);
	public void setVal(int no, double val);
	public void addToVar(int no, double val);
	
	// get min, max value and the exact position of homeostatis
	public double getMinVal(int no);
	public double getMaxVal(int no);
	public double getCenter(int no);
	
	// get the name of given variable
	public String getName(int no);
	
	// this is called every simulation step (it should implement the dynamic system) 
	public void updateStateModel();
	
	public Motivation getMotivation(int no);
	
	/**
	 * adds reinforcement to the variable by given name
	 * @param varName - name of the variable to reinforce
	 * @param strength - strength of reinforcement (default is 1, base is set in the XML file)
	 */
	public void reinforceUp(String varName, double strength);
	
	public double getValueByName(String name);
	
	public boolean reinforced(int no);
	
	public void checkBoundaries();
	
	/**
	 * whether the agent should inhere his inner variables in the world representation
	 * this actually is not so much clear.. 
	 * @return true if yes
	 */
	public boolean considerInnerVariables();
	
	public void updateMotivations();
	
	public double getSumOfBadDerivations();
	
	public int getReinforcementStrength(int i);
}
