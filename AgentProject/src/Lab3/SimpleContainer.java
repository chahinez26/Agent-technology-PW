package Lab3;

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
            pc.setParameter(ProfileImpl.MAIN_HOST, "localhost");
            AgentContainer container = rt.createAgentContainer(pc);

            AgentController agent2 = container.createNewAgent(
                "Agent2", "Lab3.A2", null
            );
            agent2.start();

        } catch (ControllerException e) {
            e.printStackTrace();
        }
        System.out.println("démarrage de simple container");
    }
}