Physiological State Space
====================================================

Author Jaroslav Vitku [vitkujar@fel.cvut.cz]

About
------

Unofficial part of Nengoros project, this package holds other components of our artificial agents, such as physiological state space generating intentions etc.

These ROS nodes will be used mainly in Hybrid Artificial Neural Network Systems, used in the ROS network or the Nengoros simulator ( http://nengoros.wordpress.com ). 

About this Project
-------
This project represents physiological state space for agents in the ALife domain, more information e.g. here [1].

Currently only a simple implementation of linear decay 1D state variables is used.



[1] David Kadleček, Motivation driven reinforcement learning and automatic creation of behavior hierarchies, PhD Thesis, Czech Technical University in Prague, Faculty of Electrical Engineering, 2008. 


TODO
-------

* Rename topic from MotivationReward to RewardMotivation
* Add testing of ROS reset
* How to store data (prosperity) for multiple nodes? So far in jython launch script..
* Support for randomized reset, e.g. in the `org.hanns.physiology.statespace.variables.AbsStateVariable#hardReset(boolean randomize)`. Currently, after reset the variable is put into the limbo area.
