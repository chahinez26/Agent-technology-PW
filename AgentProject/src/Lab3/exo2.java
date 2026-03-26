package Lab3;

import jade.core.Agent;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class exo2 extends Agent {

	public static void main(String[] args) throws Exception {
        Runtime rt = Runtime.instance();

        // A1 : recepteur
        Profile mainProfile = new ProfileImpl();
        mainProfile.setParameter(Profile.GUI, "true");
        AgentContainer recepteur = rt.createMainContainer(mainProfile);
        AgentController agent1 = recepteur.createNewAgent(
            "Agent1", "Lab3.A1", null
        );
        agent1.start();

        // A2 : envoyeur
        Profile simpleProfile = new ProfileImpl();
        simpleProfile.setParameter(Profile.MAIN, "false"); // simple container !
        AgentContainer envoyeur = rt.createAgentContainer(simpleProfile);
        AgentController agent2 = envoyeur.createNewAgent(
            "Agent2", "Lab3.A2", null
        );
        agent2.start();
    }
}