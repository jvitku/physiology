package massim.agent.body.physiological;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import massim.framework.util.MyLogger;

/**
 * representation of simplle physiological state space
 * 
 * to use this: 
 * 	-specify the initial values (and variables) in the xml file
 * 	-each step call in this order:
 * 		-updateStateModel()
 * 		-reinforce Up/Down(for selected variables if any)
 * 		-updateMotivations()
 * 
 * -derivation is computed as the one-step difference between previous and actual value
 * -reinforcement is evaluated when the ACTIVE change (caused only by some outer power 
 * collected throw (body or dedicated) sensors) moves actual position nearer limbo area 
 * 
 * @author jardavitku
 *
 */
public class ASimplePhysiological implements PhysiologicalStateSpace{

	// length of array containing physiological variables
	private int numVars;
	// array with the variables
	public double[] vars;				// actual value
	private double[] prevVars;			// previous values storage
	private double[] passiveChangeTo;	// passive change from prevVars (caused by state model dynamics)
	
	// array with the names
	public String[] names;
	// coefficients of dynamic system (corresponding to each var)
	public double[] coefficients;
	
	// definition of boundaries (min and max, the values where the agent should die)
	public double[] minVals;
	public double[] maxVals;
	
	public double[] resourcesObtained;			// how much resources agent gets
	public double[] reinforcementBaseStrength;	// the strength of reinforCement
	
	// exact homeostasis positions
	public int[] homeostasisPos;		// the position of homeostasis
	public int[] maxStimuli;			// the maximum stimulation generated from this variable
	public int[] a;						// motivation y is: y = min{maxMotiv, a(|x-homeostasisPos|)}
	
	// whether the agent should consider inner variables in the world representation
	public boolean inherePhysStatesInWorld;
	
	public boolean reportMSD;			// print mean state distance to optimal conditions?
	public boolean reportMCR;			// print mean cumulative reward to file?
	public boolean reportSeparately;	// whether to report each value into it's own file
	
	
	//private double[] stimualtions;			// actual values of stimulations
	
	// list of stimulations produced by this state space
	// these stimulations are being updated each sim step
	private ArrayList<Motivation> motivations; 
		// out.txt
	// statistics
	private MyLogger mcrLog, msdLog;
	private int counter = 0; 		// number of steps made by this instance of class
	public String dataPath;			// path to generated files
	public String experimentname;	// the name of the experiment (for prefix..)
	private String prefix = "";			// prefix of file name
	
	// 
	private boolean changed = true;
	
	@Override
	public synchronized int size() { return this.numVars; }

	@Override
	public synchronized double getVal(int no) { return vars[no]; }

	@Override
	public synchronized void setVal(int no, double val) { vars[no] = val; }

	@Override
	public synchronized void addToVar(int no, double val) { vars[no] += val; }
	
	@Override
	public synchronized String getName(int which) { return names[which]; }
	
	@Override
	public synchronized double getMinVal(int no) { return this.minVals[no]; }

	@Override
	public synchronized double getMaxVal(int no) { return this.maxVals[no]; }
	
	@Override
	public synchronized double getCenter(int no) { return this.homeostasisPos[no]; }

	@Override
	public synchronized boolean reinforced(int no) { return this.motivations.get(no).wasReinforced(); }
	
	@Override
	public synchronized Motivation getMotivation(int no) { return this.motivations.get(no); }
	
	/**
	 * update the state model 
	 * 
	 * (called BEFORE the collecting the reinforcements)
	 */
	@Override
	public synchronized void updateStateModel() {
		 
		this.counter++;
		
		for(int i=0; i<this.vars.length; i++){
			// change each value according to its dynamics specified
			this.prevVars[i] = this.vars[i];
			this.passiveChangeTo[i] = this.vars[i] + this.coefficients[i];
			this.vars[i] = this.passiveChangeTo[i];
			this.checkPassiveBoundary(i);
			this.checkBoundary(i);
		}
	}
	
	@Override
	public synchronized void reinforceUp(String varName, double strength) {
		int ind = this.findName(varName);
		if(ind == -1){
			System.err.println("ASimplePhysiologicalStateSpace: warning: variable with name: "+
					varName+" was ot found in the phys. state space, ignoring reinforcement!");
			return;
		}
		// reinforcement received
		this.vars[ind] = this.passiveChangeTo[ind] + strength*this.resourcesObtained[ind];
		this.checkBoundary(ind);
	}


	/**
	 * called after AFTER the stateModelUpdate and collecting the reinforcements) 
	 */
	public synchronized void updateMotivations(){
		
		// ignore state model dynamics, evaluate the active change
		this.evaluateReinforcements();
		
		this.computeStimulationsAndDerivations();
		
		//this.printIt();
		this.reportMCR();
		this.reportMSD();
		this.reportAll();	// report separately?
	}
	
	protected void printIt(){
		for(int i=0; i<this.numVars; i++){
			System.out.println(i+" derivation: "+this.motivations.get(i).getDerivation()+
					" stimualtion: "+this.motivations.get(i).get()+
					" reinforced: "+this.motivations.get(i).wasReinforced()+
					" prev "+this.prevVars[i]+" actual: "+this.vars[i]+"" +
							" passive change to: "+this.passiveChangeTo[i]);
		}
	}
	
	/**
	 * if the actual value of given variable is nearer to the limbo area than the previous
	 * we have made something good, it means we get the reinforcement
	 */
	private void evaluateReinforcements(){
		
		double actual, prev;
		
		for(int i=0; i<this.numVars; i++){
			actual = this.absDistFromLimboArea(i, this.vars[i]);
			prev = this.absDistFromLimboArea(i, this.passiveChangeTo[i]);
			
			// something positive has been made?
			if(actual < prev){
				this.motivations.get(i).setReinforced();// not necessary
				this.motivations.get(i).setReinforcementVal(this.reinforcementBaseStrength[i]);
				
			}else{
				this.motivations.get(i).discardReinforcement();
			}
		}
	}
	
	private double absDistFromLimboArea(int index, double val){
		return Math.abs(val-this.homeostasisPos[index]);
	}
	
	private boolean inited = false;
	
	/**
	 * print values of all physiological state variables into their separate files
	 */
	private void reportAll(){
		if(!this.reportSeparately)
			return;
		
		if(!this.inited)
			this.initReporting();
		
		for(int i=0; i<this.loggers.size(); i++)
			this.loggers.get(i).pl(10, vars[i]+"");
	}
	
	private ArrayList<MyLogger> loggers;
	
	private void initReporting(){
		this.loggers = new ArrayList<MyLogger>();
		
		// generate unique name
		DateFormat dateFormat = new SimpleDateFormat("dd.MM_HH-mm-ss");
        Date date = new Date();
        String dt = dateFormat.format(date);
        prefix = (experimentname +"__"+ dt+"_");
		
		MyLogger tmp; 
		
		for(int i=0; i<this.names.length; i++){
			tmp = new MyLogger(dataPath+prefix+this.names[i]+".txt");
			tmp.printToFile(true);
			this.loggers.add(tmp);
		}
		
		this.inited = true;
	}
	
	
	
	
	/**
	 * print Mean Cumulative Reward to file for Matlab
	 * note: these functions print only actual distance/reward !!!!
	 */
	private void reportMCR(){
		if(!this.reportMCR)
			return;
		
		int sum = 0;
		
		for(int i=0; i<this.numVars; i++){
			if(this.motivations.get(i).wasReinforced())
				sum++;
		}
		this.mcrLog.pl(0, Integer.toString(sum));
	}
	
	/**
	 * print Mean State Distance to optimal conditions to file for Matlab
	 * note: these functions print only actual distance/reward !!!!
	 */
	private void reportMSD(){
		if(!this.reportMSD)
			return;
		
		double sum = 0;
		
		// compute the MSD value
		for(int i=0; i<this.numVars; i++)
			sum = sum + Math.abs(this.vars[i]-this.homeostasisPos[i]);
		
		sum = sum / this.numVars;
		this.msdLog.pl(0, Double.toString(sum));
	}
	
	/**
	 * compute motivations generated by particular variables
	 */
	private void computeStimulationsAndDerivations(){

		double deriv;
		
		double y;
		double generated;
		double dist;
		
		for(int i=0; i<this.vars.length; i++){
			
			// derivations 
			deriv = this.vars[i] - this.prevVars[i];
			this.motivations.get(i).setDerivation(deriv);
			
			// stimulations			
			dist = Math.abs( this.vars[i] - this.homeostasisPos[i] );
			generated = this.a[i] * dist;
			
			y = Math.min(this.maxStimuli[i], generated);
			
			this.motivations.get(i).set(y);
		}
	}
	
	/**
	 * derivation is change of state space variable
	 * bad derivation is such change that has moved the actual value from the homeostasis
	 * 
	 * @return sum of such derivations
	 */
	public double getSumOfBadDerivations(){
		double sum = 0;
		// for all state variables
		for(int i=0; i<this.numVars; i++){
			double distNow = Math.abs( this.vars[i] - this.homeostasisPos[i] );
			double distPrev = Math.abs( this.prevVars[i] - this.homeostasisPos[i] );

			if(distNow > distPrev){
				System.out.println("BAAAAAAAAAD derivation! ");
				sum = sum + this.motivations.get(i).getDerivation();
			}
		}
		return sum;
	}
	
	/**
	 * compute the maximum stimulation that can be generated 
	 */
	public synchronized double getMaxMotivation(int no){
		double out = 0;
		
		// get bigger distance from homeostasis to some bound
		double biggerDist = 0;
		
		if(Math.abs(this.homeostasisPos[no] - this.maxVals[no]) > biggerDist)
			biggerDist = Math.abs(this.homeostasisPos[no] - this.maxVals[no]);
		
		if(Math.abs(this.homeostasisPos[no] - this.minVals[no]) > biggerDist)
			biggerDist = Math.abs(this.homeostasisPos[no] - this.minVals[no]);
		
		// get the max value, we know the inclination and distance from zero (homeostasis)
		out = a[no] * biggerDist; 
		
		// the stimulation can be saturated
		out = Math.min(out, this.maxStimuli[no]); 
		
		return out;
	}
	
	
	private void checkPassiveBoundary(int i){
		
		if(this.passiveChangeTo[i] < this.minVals[i]){
			//System.out.println("Warning: not enough of: "+this.names[i]+" agent should die..");
			this.passiveChangeTo[i] = this.minVals[i];
		}
		else if(this.passiveChangeTo[i] > this.maxVals[i]){
			//System.out.println("Warning: too much of: "+this.names[i]+" agent should die..");
			this.passiveChangeTo[i] = this.maxVals[i];
		}
}
	
	private void checkBoundary(int i){
		
			if(this.vars[i] < this.minVals[i]){
				//System.out.println("Warning: not enough of: "+this.names[i]+" agent should die..");
				this.vars[i] = this.minVals[i];
			}
			else if(this.vars[i] > this.maxVals[i]){
				//System.out.println("Warning: too much of: "+this.names[i]+" agent should die..");
				this.vars[i] = this.maxVals[i];
			}
	}

	//@Override
	public void init() {
		this.numVars = this.names.length;
		
		if((this.numVars != this.minVals.length) ||
				(this.numVars != this.maxVals.length) ||
				(this.numVars != this.vars.length) ||
				(this.numVars != this.coefficients.length))

			System.err.println("ERROR: ASimplePhysiologicalStateSpace: " +
					"all arrays must have the same length ");
		
		
		for(int i=0; i<this.numVars; i++){
			if(this.homeostasisPos[i] > this.maxVals[i] || this.homeostasisPos[i] < this.minVals[i] )
				System.err.println("ASimplePhysiological:init: homeostasis position["+i+"]out of bounds!");
			if(this.maxStimuli[i] < 0)
				System.err.println("ASimplePhysiological:init: maxMotivation["+i+"] smaller than zero!");
				
		}
		
		this.motivations = new ArrayList<Motivation>(this.numVars);
		for(int i=0;i<this.numVars; i++)
			this.motivations.add(i, new Motivation(this.names[i], this.getMaxMotivation(i)));
		
		this.prevVars = new double[this.numVars];
		this.passiveChangeTo = new double[this.numVars];
		for( int i=0; i<this.numVars; i++){
			this.prevVars[i] = this.vars[i];
			this.passiveChangeTo[i] = this.vars[i];
		}
		
		this.computeStimulationsAndDerivations();
		this.evaluateReinforcements();
		
		this.initLogging();
	}
	
	private void initLogging(){
		// generate unique name
		DateFormat dateFormat = new SimpleDateFormat("dd.MM_HH-mm-ss");
        Date date = new Date();
        String dt = dateFormat.format(date);
        prefix = (experimentname +"__"+ dt+"_");
        
        if(this.reportMCR){
			this.mcrLog = new MyLogger(dataPath+prefix+"mcr.txt");
			this.mcrLog.printToFile(true);
		}
		if(this.reportMSD){
			this.msdLog = new MyLogger(dataPath+prefix+"msd.txt");
			this.msdLog.printToFile(true);
		}	
	}



	@Override
	public synchronized double getValueByName(String name) {
		int ind = this.findName(name);
		if(ind == -1){
			System.err.println("ASimplePhysiologicalStateSpace: getValueByName: variable with name: "+
					name+" was ot found in the phys. state space, ignoring reinforcement!");
			return -1;
		}
		return this.vars[ind];
	}


	/**
	 * this finds the variable with the given name
	 * 
	 * @param name
	 * @return - index of variable or -1 if not found
	 */
	private int findName(String name){
		for(int i=0; i<this.numVars; i++){
			if(name.equalsIgnoreCase(this.names[i]))
				return i;
		}
		return -1;
	}

	
	@Override
	public synchronized ArrayList<Motivation> getMotivations() { return this.motivations; }

	@Override
	public synchronized boolean variableAdded() { return this.changed; }
	
	
	protected synchronized void indicateChnge(){ this.changed = true; }

	@Override
	public synchronized void discardVariableAdded() { this.changed = false; }

	@Override
	public synchronized boolean considerInnerVariables() { return inherePhysStatesInWorld; }

	public synchronized void checkBoundaries(){
		
		for(int i=0; i<this.vars.length; i++){
			if(this.vars[i] < this.minVals[i]){
				//System.out.println("Warning: not enough of: "+this.names[i]+" agent should die..");
				this.vars[i] = this.minVals[i];
			}
			else if(this.vars[i] > this.maxVals[i]){
				//System.out.println("Warning: too much of: "+this.names[i]+" agent should die..");
				this.vars[i] = this.maxVals[i];
			}
		}
	}

	@Override
	public String getDataPath() { return this.dataPath; }

	@Override
	public String getExperimentName() { return this.experimentname; }

	@Override
	public int getReinforcementStrength(int i) {
		return (int)this.reinforcementBaseStrength[i];
	}



}
