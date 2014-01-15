package massim.agent.mind.intentions;

import massim.agent.body.physiological.Motivation;
import massim.agent.mind.harm.components.predictors.HarmSystemSettings;
import massim.agent.mind.harm.variables.Variable;
import massim.agent.mind.harm.variables.VariableList;
import massim.framework.util.MyLogger;


/**
 * variable in the intentional state space
 * 
 * @author jardavitku
 *
 */
public class Var {

	private final int reinfStrength;
	private final double coeff;
	private final double a;
	
	private final int min;
	private final int max;
	
	private double value;
	
	private final Motivation m;
	private final String name;
	private final Variable myVar;
	
	private final MyLogger log;
	private final int LEV = 15;
	
	public Var(HarmSystemSettings set, Variable var, MyLogger log, int min, int max){
		this.reinfStrength = set.intReinforcementBaseStrength;
		this.coeff = set.intCoefficient;
		this.a = set.intA;
		
		this.value = 0;
		this.name = var.getName();
		this.m = new Motivation(name, max*this.a);
		this.myVar = var;
		
		this.log = log;
		
		this.min = min;
		this.max = max;
	}
	
	private void tryToReinforce(int step){
		
		if(this.checkIfChangedNow(step)){
			this.mypl("Reinforcement detected!!");
			this.value = 0;//this.value + reinfStrength;
			this.m.setReinforced();
			this.m.setReinforcementVal(reinfStrength);
		}else{
			this.m.discardReinforcement();
		}
	}
	
	public String getName(){ return this.name; }
	
	private boolean checkIfChangedNow(int step){
		
		String prev="", actual="";	// prev value and the actual one
		boolean pFound, aFound;
		
		aFound = false;
		pFound = false;
		
		Variable v = this.myVar;
		
		for(int j=0; j<v.getNumValues(); j++){
					
			// if this value last seen previous step 
			if((step-v.vals.get(j).lastSeen()) == 1){
				prev = v.vals.get(j).getStringVal();
				pFound = true;
				if(aFound){
					if(!prev.equalsIgnoreCase(actual)){
						this.mypl(" chenge detected!" );
						return true;
					}
				}
			}
			// if this value is actual
			else if((step - v.vals.get(j).lastSeen()) == 0){
				actual = v.vals.get(j).getStringVal();
				aFound = true;
				// if found previous also, we have found our variable
				if(pFound){
					if(!prev.equalsIgnoreCase(actual)){
						this.mypl(" chenge detected!" );
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void makeStepWithoutDynamics(int step){
		// value stays still
		
		// reinforce if necessary
		this.tryToReinforce(step);	// reinforcement allowed (necessary)
		this.checkDimensions();
		
		// set new motivation value
		this.updateMotivation();	
	}
	
	/**
	 * make all at once, dynamics and reinforcements
	 * @param step - actual step
	 */
	public void makeStep(int step){
		// move by inner dynamics
		this.value = this.value+this.coeff;
		this.checkDimensions();
		
		// reinforce if necessary
		this.tryToReinforce(step);
		this.checkDimensions();
		
		// set new motivation value
		this.updateMotivation();
		//this.mypl("motivation value set to: "+this.m.get());
	}
	
	public void setVal(int what){
		
		this.value = what;
		this.checkDimensions();
		this.updateMotivation();
		//System.out.println("var set to this and this: "+this.getName()+" "+what+" "+this.m.get());
	}
	
	public double value(){ return this.value; }
	
	public Motivation getMotivation(){ return this.m; }
	
	public Variable getVar(){ return this.myVar; } 
	
	private void checkDimensions(){
		if(this.value < min)
			this.value = min;
		if(this.value > max)
			this.value = max;
	}
	
	private void updateMotivation(){
		this.m.set(this.value*this.a);
	}
	
	private void mypl(String what){ this.log.pl(LEV,"VAR: "+this.name+": "+what); }
	
}
