package Lab3;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class A31 extends Agent {

    @Override
    protected void setup() {

        System.out.println("Receiver started");

        // Filtre INFORM
        MessageTemplate mtInform =
                MessageTemplate.MatchPerformative(
                        ACLMessage.INFORM);

        ACLMessage informMsg =
                blockingReceive(mtInform);

        System.out.println(
                "INFORM reçu : "
                + informMsg.getContent()
        );

        // Filtre PROPOSE
        MessageTemplate mtPropose =
                MessageTemplate.MatchPerformative(
                        ACLMessage.PROPOSE);

        ACLMessage proposeMsg =
                blockingReceive(mtPropose);

        System.out.println(
                "PROPOSE reçu : "
                + proposeMsg.getContent()
        );
    }
}