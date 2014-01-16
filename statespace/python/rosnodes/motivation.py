# Create the NeuralModule which implements discrete source of motivation.
#
# the source of motivaiton has:
#   -n inputs, these are summed, if the value is equal or more  than threshold (1 now), the reinforcement is received, state variable is set back to limbo
#   -2 outputs defining: {reward just received, size of current motivation}
#
# by Jaroslav Vitku [vitkujar@fel.cvut.cz]

import nef
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule

#from org.hanns.rl.discrete.ros.sarsa import QLambda as QLambda
from org.hanns.physiology.statespace.ros import BasicMotivation as Motivation

# java classes
classs = "org.hanns.physiology.statespace.ros.BasicMotivation"


# Synchronous NeuralModule implementing simple source of agents motivation
def basic(name, noInputs=1, decay=Motivation.DEF_DECAY, logPeriod=100):

	command = [classs, '_'+Motivation.noInputsConf+ ':=' + str(noInputs), 
	'_'+Motivation.decayConf+':='+str(decay),
	'_'+Motivation.logPeriodConf+':='+str(logPeriod)]

	g = NodeGroup("Motivation", True);
	g.addNode(command, "Motivation", "java");
	module = NeuralModule(name+'_Motivation', g, False)

	module.createEncoder(Motivation.topicDecay,"float",1); 				# decay config

    #TODO
	#module.createDecoder(QLambda.topicProsperity,"float",1);			# float[]{prosperity, coverage, reward/step}

	module.createDecoder(QLambda.topicDataOut, "float", 2)              # decode float[]{reward,motivation}
	module.createEncoder(Motivation.topicDataIn, "float", noInputs) 	# encode input data

	return module
	
def basicConfigured(name, net, noInputs=1, decay=Motivation.DEF_DECAY, logPeriod=100):

	# build the node
	bb = basic(name, noInputs, decay, logPeriod)
	net.add(bb)

	# define the configuration
	net.make_input('decay',[Motivation.DEF_DECAY])

	# wire it
	net.connect('dcay', mod.getTermination(Motivation.topicDecay))

	return mod
