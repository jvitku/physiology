# Create the NeuralModule which implements discrete source of motivation.
#
# the source of motivaiton has:
#   -n inputs, these are summed, if the value is equal or more  than threshold (1 now), the reinforcement is received, state variable is set back to limbo
#   -2 outputs defining: {reward just received, size of current motivation}
#
# by Jaroslav Vitku [vitkujar@fel.cvut.cz]

import nef
from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils
from org.hanns.physiology.statespace.ros import BasicMotivation as Motivation
import motivation

net=nef.Network('Motivation source')
net.add_to_nengo()  

#RosUtils.setAutorun(False)     # Do we want to autorun roscore and rxgraph? (tru by default)
#RosUtils.prefferJroscore(True)  # preffer jroscore before the roscore? 

# 1 input, decay default, log period 1
finderA = motivation.basicConfigured("RL", net, 1, Motivation.DEF_DECAY,1)   

#Create a white noise input function with params: baseFreq, maxFreq [rad/s], RMS, seed
# first dimension is reward, do not generate signal (ignored in the connection matrix)
generator=FunctionInput('StateGenerator', [FourierFunction(0,0,0,12),
    FourierFunction(.5, 11,1.6, 17),FourierFunction(.2, 21,1.1, 11)],Units.UNK) 

# first dimension is reward, do not generate states (these are ignored in the conneciton matrix)
reward=FunctionInput('RewardGenerator', [FourierFunction(.1, 10,1, 12),
        FourierFunction(0,0,0, 17),FourierFunction(0,0,0, 17),],Units.UNK)

net.add(generator)
net.add(reward)

# data
#net.connect(generator,	finderA.newTerminationFor(QLambda.topicDataIn,[0,1,1]))
#net.connect(reward,		finderA.newTerminationFor(QLambda.topicDataIn,[1,0,0]))


print 'Configuration complete.'
