package Lab3;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class A1 extends Agent {
    protected void setup() {
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("I am " + getLocalName() + 
                        ". I have received the message " + msg.getContent() + 
                        " from the agent " + msg.getSender().getName());

                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("Thank you");
                    send(reply);
                } else {
                    block();
                }
            }
        });
    }
}