package Lab3;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import jade.core.messaging.TopicManagementHelper;

public class exo42 extends Agent {
    protected void setup() {
        try {
            // Créer le topic "JADE"
            TopicManagementHelper topicHelper =
                (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
            final AID topic = topicHelper.createTopic("JADE");

            // Envoyer un message toutes les 5 secondes
            addBehaviour(new TickerBehaviour(this, 5000) {
                public void onTick() {
                    System.out.println("Agent " + myAgent.getLocalName() +
                        ": Sending message about topic " + topic.getLocalName());
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(topic);
                    msg.setContent(String.valueOf(getTickCount()));
                    myAgent.send(msg);
                }
            });
        } catch (Exception e) {
            System.err.println("Agent " + getLocalName() +
                ": ERROR creating topic \"JADE\"");
            e.printStackTrace();
        }
    }
}