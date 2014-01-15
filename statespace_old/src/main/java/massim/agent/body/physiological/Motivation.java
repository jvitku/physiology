package massim.agent.body.physiological;

/**
 * this class represents single motivation from an arbitrary source (physiological, intentional..)
 * 
 * @author jardavitku
 *
 */
public class Motivation {
	
//	private final Action a;	// pointer to my action (long history in the ConnectionManager)
	
	private final String name;
	private double actualVal;
	private double derivation;
	private boolean reinforced;
	private int reinfVal;
	
	private final double maxVal;
	
	private boolean setreq;	// request (the motivation has been manually set to the different value!
	
	public Motivation(String name, double val, double maxVal){
		this.actualVal = val;
		this.derivation = 0;
		this.reinforced = false;
		this.name = name;
		this.reinfVal = 0;
		this.maxVal = maxVal;
	}
	
	public Motivation(String name, double maxVal){
		this.actualVal = 0;
		this.derivation = 0;
		this.reinforced = false;
		this.name = name;
		this.reinfVal = 0;
		this.maxVal = maxVal;
	}
	
	// set the flag that this has been changed manually!
	public synchronized void set(double val){
		this.setreq = true;
		this.actualVal = val;
	}
	
	public synchronized boolean requestIsSet(){
		return this.setreq;
	}
	public synchronized void discardReq(){
		this.setreq = false;
	}
	/**
	 * get motivation to execute action connected
	 * @return
	 */
	public synchronized double get(){ return this.actualVal; }

	/**
	 * reinforcement jsut received?
	 * @return
	 */
	public synchronized boolean wasReinforced(){ return this.reinforced; }
	
	/**
	 * entire change of my variable value 
	 * @return
	 */
	public synchronized double getDerivation(){ return this.derivation; }
	
	/**
	 * low level, do not touch this 
	 */
	public synchronized void setReinforced(){ this.reinforced = true; }
	public synchronized void discardReinforcement(){ 
		this.reinforced = false;
		this.reinfVal = 0;
	}
	
	public synchronized void setDerivation(double derivation){ this.derivation = derivation; }
	
	public synchronized String name(){ return this.name; }
	
	public synchronized void setReinforcementVal(double val){
		this.reinforced = true;
		this.reinfVal = (int)val;
	}
	
	public synchronized int getReinforcementVal(){
		return this.reinfVal;
	}
	
	public synchronized double getMaxMotivation(){ return this.maxVal; }
	
}
