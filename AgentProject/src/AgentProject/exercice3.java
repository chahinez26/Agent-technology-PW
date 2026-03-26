package AgentProject;
import jade.core.Agent;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class exercice3 extends Agent {

	public static void main(String[] args) {
		try {
			Runtime rt = Runtime.instance();
			ProfileImpl p = new ProfileImpl("localhost", 1104, "JADE");
			p.setParameter(ProfileImpl.GUI, "true");
			ContainerController mc = rt.createMainContainer(p);
			AgentController ag1 = mc.createNewAgent("Buyer1", "AgentProject.exercice3", null);
			ag1.start();
		} catch (Exception e) { e.printStackTrace(); }
	}

}
