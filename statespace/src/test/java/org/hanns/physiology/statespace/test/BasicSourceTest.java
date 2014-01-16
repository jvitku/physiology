package org.hanns.physiology.statespace.test;

import static org.junit.Assert.*;

import org.hanns.physiology.statespace.motivationSource.AbsSource;
import org.hanns.physiology.statespace.motivationSource.impl.BasicSource;
import org.hanns.physiology.statespace.transformations.Transformation;
import org.hanns.physiology.statespace.transformations.impl.Sigmoid;
import org.hanns.physiology.statespace.variables.AbsStateVariable;
import org.hanns.physiology.statespace.variables.StateVariable;
import org.hanns.physiology.statespace.variables.impl.LinearDecay;
import org.junit.Test;

public class BasicSourceTest {


	/**
	 * Try basic funcitonality of basic state variable:
	 * -initialize (in limbo area, no big motivation)
	 * -make few steps, check motivation increates, check values
	 * -make few steps, check motivaion is max
	 * -send reward, check variable in limbo, motivation near 0
	 * 
	 */
	@Test
	public void basic(){

		Transformation t = new Sigmoid();
		StateVariable var = new LinearDecay(1,0.1);

		BasicSource s = new BasicSource(var,t);

		// after initialization
		assertTrue(s.getMotivation() == AbsSource.DEF_MIN);
		assertTrue(s.getReinforcement()==0);
		assertTrue(var.getValue() == AbsStateVariable.DEF_LIMBO);

		// after reset
		s.softReset(false);
		assertTrue(s.getMotivation() == AbsSource.DEF_MIN);
		assertTrue(s.getReinforcement()==0);
		assertTrue(var.getValue() == AbsStateVariable.DEF_LIMBO);

		s.makeStep(new float[]{0});
		assertTrue(s.getReinforcement()==0);
		assertTrue(s.getMotivation()<0.01);
		
		float prev = s.getMotivation();
		
		// make few steps..
		for(int i=1; i<5; i++){
			s.makeStep(new float[]{0});
			System.out.println("variable is : "+var.getValue()+
					",motivation is now: "+s.getMotivation());
			assertTrue(s.getMotivation()>prev);
			prev = s.getMotivation();
			assertTrue(s.getReinforcement()==0);
		}

		// after 5 steps, we should be in the middle of motivation
		// (SIgmoid)
		assertTrue(s.getMotivation()==0.5);
		

		// make few steps..
		for(int i=0; i<5; i++){
			s.makeStep(new float[]{0});
			System.out.println("variable is : "+var.getValue()+
					",motivation is now: "+s.getMotivation());
			assertTrue(s.getMotivation()>prev);
			prev = s.getMotivation();
			assertTrue(s.getReinforcement()==0);
		}
		// motivation should be near the maximum
		assertTrue(s.getMotivation()>0.98);
		
		s.makeStep(new float[]{1});
		
		assertTrue(s.getMotivation()<0.01);
		assertTrue(s.getReinforcement()>=1);

	}

}
