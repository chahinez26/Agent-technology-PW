package Lab3;

import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

public class Maincontainer {
    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.instance();

            ProfileImpl pc = new ProfileImpl();
            pc.setParameter(ProfileImpl.GUI, "true");  
            AgentContainer container = rt.createMainContainer(pc);

            AgentController agent1 = container.createNewAgent(
                "Agent1", "Lab3.A1", null
            );
            agent1.start();

            System.out.println("Main container started");
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }
}




