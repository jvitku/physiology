package massim.agent.mind.intentions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import massim.agent.body.actionset.ActionSet;
import massim.agent.body.agentWorldInterface.actuatorLayer.actuators.ActuatorHARM;
import massim.agent.body.physiological.Motivation;
import massim.agent.body.physiological.PhysiologicalStateSpace;
import massim.agent.mind.harm.actions.ActionList;
import massim.agent.mind.harm.actions.RootDecisionSpace;
import massim.agent.mind.harm.components.hierarchy.util.ActionVariableFilter;
import massim.agent.mind.harm.components.hierarchy.util.HierarchyComponent;
import massim.agent.mind.harm.components.predictors.HarmSystemSettings;
import massim.agent.mind.harm.variables.PropertyList;
import massim.agent.mind.harm.variables.Variable;
import massim.agent.motivationActionMapping.ConnectionManager;
import massim.framework.util.MyLogger;
import massim.shared.SharedData;

public class ASimpleIntentions implements IntentionalStateSpace, HierarchyComponent{
	
	private final int LEV = 15;
	private final MyLogger log;
	
	//private final ActionVariableFilter filter;	// stores info about the last eecuted primitive act.
	
	//private final RootDecisionSpace root;
	private final HarmSystemSettings settings;
	
	//private final ActuatorHARM actuator;
	private int step;
	
	//private final ConnectionManager connections;	// holds connections between motivations and actions
	private final SharedData shared;

	private final ArrayList<Var> vars;
	
	private final double a;
	
	
	private final int min = 0;
	private final int max = 3;
	
	private final boolean isUsed;	// whether to use this
	
	private ArrayList<Motivation> motivs;// redundancy, we have to return this
	
	private final boolean reportMSD;
	private final int reportSeparately;
	private final String path;
	private final String expname;	
	private MyLogger msdLog;
	private int counter = 0;
	
	
	public ASimpleIntentions(ActionSet actuators, PropertyList worldProperties, HarmSystemSettings set,
		MyLogger log, SharedData shared){
		
		this.log = log;
		
		this.settings = set;
		//this.connections = connections;
		this.shared = shared;
		this.step = 0;
	
		if(this.settings.useInt)
			log.pl(LEV,"IntentionalStateSpace: initializing, will be used!");
		
		this.vars = new ArrayList<Var>();
		this.a = this.settings.intA;
		
		this.isUsed = this.settings.useInt;
		
		this.motivs = new ArrayList<Motivation>();
		this.reportMSD = this.settings.reportIntMSD;
		this.reportSeparately = this.settings.reportSeparately;
		this.path = this.settings.dataPath;
		this.expname = this.settings.experimentname;
		
		if(this.reportMSD){
			DateFormat dateFormat = new SimpleDateFormat("dd.MM_HH-mm-ss");
	        Date date = new Date();
	        String dt = dateFormat.format(date);
	        String prefix = (expname+"__"+ dt+"_");
	        
			this.msdLog = new MyLogger(path+prefix+"msdInt.txt");
			this.msdLog.printToFile(true);
		}
	}
	
	public boolean isUsed(){ return this.isUsed; }

	private boolean forFirst = true;	// for first in planning mode?
	
	/**
	 * for all variables: move by it's own dynamics
	 * then check whether reinforcement came (change of my variable) and if yes reinforce it 
	 */
	private synchronized void updateStateModel(){
		
		
		if(this.shared.inPlanExecutionMode()){
			//System.out.println("intentions: in execution mode");
			
			// for all intentions, check the motivation change, if changed, update intention
			Var v;
			for(int i=0; i<this.vars.size(); i++){
				v = this.vars.get(i);
				if(v.getMotivation().requestIsSet()){
					if(v.getMotivation().get() > 0)	// motivated to the high value?
						v.setVal(Integer.MAX_VALUE);
					else
						v.setVal(0);
					v.getMotivation().discardReq();
				}
				v.makeStepWithoutDynamics(this.step);
			}
			this.reportMSD();
			this.reportSeparately();
			return;
		}
		
		// in the manual mode, all intentions are set to zero
		if(this.shared.inPlanningMode()){
			if(this.forFirst){
				this.forFirst = false;
				this.disableIntentions();
				
			}
			this.reportMSD();
			this.reportSeparately();
			//System.out.println("intentions: in planning mode");
			return;
		}
		forFirst = true;
		
		for(int i=0; i<this.vars.size(); i++){
			/*
			this.mypl("updating this: "+i+" "+this.vars.get(i).getVar().getName()+" val: "+
					this.vars.get(i).value());
					*/
			this.vars.get(i).makeStep(this.step);
		}
		
		this.reportMSD();
		this.reportSeparately();
	}
	
	private void disableIntentions(){
		for(int i=0; i<this.vars.size(); i++){
			this.vars.get(i).setVal(0);
		}
	}
	
	public synchronized Motivation addIntention(Variable v){
		this.mypl("adding variable this: "+v.getName());
		Var vv = new Var(this.settings, v, this.log, this.min, this.max);
		this.vars.add(vv);
		this.motivs.add(vv.getMotivation());
		
		// return the newly created motivation
		return this.vars.get(this.size()-1).getMotivation();
	}
	
	
	/**
	 * print Mean State Distance to optimal conditions to file for Matlab
	 * note: these functions print only actual distance/reward !!!!
	 */
	private void reportMSD(){
		if(!this.reportMSD)
			return;
		
		double sum = 0;
		
		
		if(this.size()==0)
			sum = 0;
		else{
			// compute the MSD value
			for(int i=0; i<this.size(); i++)
				sum = sum + Math.abs(0-this.vars.get(i).value());
			
			sum = sum / this.size();
		}
		this.msdLog.pl(0, Double.toString(sum));
	}
	
	private boolean inited;
	
	/**
	 * print the max number of motivations separately 
	 * (max num is given by the val of printSeparately)
	 */
	private void reportSeparately(){
		// do not print?
		if(this.reportSeparately <= 0)
			return;
		
		if(!this.inited)
			this.init();
		
		for(int i=0; i<this.loggers.size(); i++){
			if(vars.size()>i)
				this.loggers.get(i).pl(10, this.vars.get(i).value()+"");
			else
				this.loggers.get(i).pl(10, this.defVal+"");
		}
		
	}
	
	private double defVal = -1;	// print this if no intention added to the logger
	private ArrayList<MyLogger> loggers;
	
	private void init(){
		this.loggers = new ArrayList<MyLogger>();

		DateFormat dateFormat = new SimpleDateFormat("dd.MM_HH-mm-ss");
        Date date = new Date();
        String dt = dateFormat.format(date);
        String prefix = (expname+"__"+ dt+"_");
        
		MyLogger tmp; 
		
		for(int i=0; i<this.reportSeparately; i++){
			tmp = new MyLogger(path+prefix+"intention_"+i+".txt");
			tmp.printToFile(true);
			this.loggers.add(tmp);
		}
		this.inited = true;
	}
	
	@Override
	public int size() {
		return this.vars.size();
	}

	@Override
	public ArrayList<Motivation> getMotivations() {
		return this.motivs;
	}

	@Override
	public double getMaxMotivation(int no) {
		return this.a*3;
	}

	@Override
	public boolean variableAdded() {
		return false;
	}

	@Override
	public void discardVariableAdded() {
	}

	@Override
	public String getDataPath() {
		return null;
	}

	@Override
	public String getExperimentName() {
		return null;
	}

	@Override
	public void preSimulationStep() {
		this.step++;
		this.updateStateModel();
	}

	@Override
	public void postSimulationStep() {
	}
	
	private void mypl(String what){ this.log.pl(LEV,"IntentionalStateSpace: "+what); }

	
	public void setVal(int which, int val){
		if(which>=this.vars.size()){
			System.err.println("ERROR: ASimpleIntentions: setVal: index out of bounds!");
			return;
		}
		this.vars.get(which).setVal(val);
	}

	public boolean reinforced(int i){
		return this.vars.get(i).getMotivation().wasReinforced();
	}
	
	public Motivation getMotivation(int i){
		if(i >= this.vars.size()){
			System.err.println("ERROR: ASimpleIntentions: getMotivation: index out of bounds!");
			return null;
		}
		return this.vars.get(i).getMotivation();
	}
	
	public Variable getVar(int i){
		if(i >= this.vars.size()){
			System.err.println("ERROR: ASimpleIntentions: getMotivation: index out of bounds!");
			return null;
		}
		return this.vars.get(i).getVar();
	}
	
	
	public int getMinVal(){
		return this.min;
	}
	
	public int getMaxVal(){
		return this.max;
	}
	
	public double getVal(int i){
		return this.vars.get(i).value();
	}
	
	public String getName(int i){
		return this.vars.get(i).getName();
	}

}
