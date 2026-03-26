package Lab3;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

public class A2 extends Agent {
    protected void setup() {
        // Envoyer "Hello" à Agent1
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("Agent1", AID.ISLOCALNAME));
        msg.setContent("Hello");
        send(msg);

        // Attendre la réponse de Agent1
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage reply = receive();
                if (reply != null) {
                    System.out.println("I am " + getLocalName() + 
                        " I have received " + reply.getContent() + 
                        " from the agent " + reply.getSender().getName());
                } else {
                    block();
                }
            }
        });
    }
}