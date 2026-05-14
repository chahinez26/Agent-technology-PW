package Lab4;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class MainContainer4 {
	public static void main(String[] args) throws Exception {
        Runtime rt = Runtime.instance();
        ProfileImpl pc = new ProfileImpl();
        pc.setParameter(ProfileImpl.GUI, "true");

        AgentContainer container = rt.createMainContainer(pc);

        // Lancer 2 agents
        AgentController a1 = container.createNewAgent(
            "a1", "Lab4.parallel_behaviour", null
        );
        AgentController a2 = container.createNewAgent(
            "a2", "Lab4.parallel_behaviour", null
        );

        a1.start();
        a2.start();
    }
}
