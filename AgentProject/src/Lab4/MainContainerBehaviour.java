package Lab4;

import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;


public class MainContainerBehaviour {
	public static void main(String[] args) throws Exception {
        Runtime rt = Runtime.instance();
        ProfileImpl pc = new ProfileImpl();
        pc.setParameter(ProfileImpl.GUI, "true");

        AgentContainer container = rt.createMainContainer(pc);

        AgentController agent = container.createNewAgent(
            "MonAgent", "Lab4.AgentBehaviour", null
        );
        agent.start();
    }
}
