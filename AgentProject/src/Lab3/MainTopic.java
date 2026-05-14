package Lab3;

import jade.core.Agent;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class MainTopic extends Agent {
    public static void main(String[] args) throws Exception {
        Runtime rt = Runtime.instance();

        // Activer le service Topic dans le profil
        Profile mainProfile = new ProfileImpl();
        mainProfile.setParameter(Profile.GUI, "true");
        mainProfile.setParameter(Profile.SERVICES,
            "jade.core.messaging.TopicManagementService"); // ← clé du problème

        AgentContainer mainContainer = rt.createMainContainer(mainProfile);

        // Récepteurs en premier
        AgentController receiver1 = mainContainer.createNewAgent(
            "Receiver1", "Lab3.exo41", null
        );
        AgentController receiver2 = mainContainer.createNewAgent(
            "Receiver2", "Lab3.exo41", null
        );
        receiver1.start();
        receiver2.start();

        Thread.sleep(1000);

        // Émetteur
        AgentController sender = mainContainer.createNewAgent(
            "Sender", "Lab3.exo42", null
        );
        sender.start();
    }
}