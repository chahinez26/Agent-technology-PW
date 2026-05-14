package Lab3;

import jade.core.Agent;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class MainExo5 extends Agent {
    public static void main(String[] args) throws Exception {
        Runtime rt = Runtime.instance();

        Profile mainProfile = new ProfileImpl();
        mainProfile.setParameter(Profile.GUI, "true");
        AgentContainer mainContainer = rt.createMainContainer(mainProfile);

        // Buyer en premier (doit être prêt à recevoir)
        AgentController buyer = mainContainer.createNewAgent(
            "Buyer", "Lab3.B2", null
        );
        buyer.start();

        Thread.sleep(500); // attendre que Buyer soit prêt

        // Seller ensuite (envoie dès le setup)
        AgentController seller = mainContainer.createNewAgent(
            "Seller", "Lab3.S2", null
        );
        seller.start();
    }
}