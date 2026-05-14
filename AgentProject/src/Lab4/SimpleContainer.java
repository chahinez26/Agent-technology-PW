package Lab4;

import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

public class SimpleContainer {
    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.instance();
            ProfileImpl pc = new ProfileImpl(false);
            pc.setParameter(ProfileImpl.CONTAINER_NAME, "container_2");
            AgentContainer container = rt.createAgentContainer(pc);

            AgentController agent = container.createNewAgent(
            	    "MobileAgent", "Lab4.MobileAgent", null
            	);
            agent.start();


        } catch (ControllerException e) {
            e.printStackTrace();
        }
        System.out.println("démarrage de simple container");
    }
}
