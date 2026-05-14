package Lab3;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class A32 extends Agent {

    @Override
    protected void setup() {

        // ===== INFORM =====
        ACLMessage msg1 =
                new ACLMessage(ACLMessage.INFORM);

        msg1.addReceiver(
                new AID("Agent1", AID.ISLOCALNAME)
        );

        msg1.setContent("Bonjour Agent2");

        send(msg1);

        // ===== PROPOSE =====
        ACLMessage msg2 =
                new ACLMessage(ACLMessage.PROPOSE);

        msg2.addReceiver(
                new AID("Agent1", AID.ISLOCALNAME)
        );

        msg2.setContent("Bonjour Agent1");

        send(msg2);

        System.out.println("Messages envoyés");
    }
}