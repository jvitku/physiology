Representation of other Agents Components
====================================================

Author Jaroslav Vitku [vitkujar@fel.cvut.cz]

About
------

Unofficial part of Nengoros project, this package holds other components of our artificial agents, such as physiological state space generating intentions etc.

These ROS nodes will be used mainly in Hybrid Artificial Neural Network Systems, used in the ROS network or the Nengoros simulator ( http://nengoros.wordpress.com ). 


Installation
------------------
The package contains ROS java nodes. The simplest how to run them is to:
	
	* Install ROS core, either as a part of official [ROS installation](http://www.ros.org/) or the one contained in the [Nengoros project](http://nengoros.wordpress.com/). 
	* Start the core (e.g. `roscore` or `cd nengoros/jroscore && ./jroscore` )
	* Start the class `org.ros.RosRun` with the parameter specifying the name of class (ROSjava node) to be launched. 
	
Simple tutorial how to do this is [here](http://nengoros.wordpress.com/tutorials/creating-new-project-with-rosjava-nodes/). 




