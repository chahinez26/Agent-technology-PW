package AgentProject;

import jade.core.Agent;

public class AgDisplay extends Agent {

	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		super.setup();
		System.out.println("Hello World! My name is :"+getLocalName());
	}

}
