package Lab3;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.io.IOException;

public class S2 extends Agent {
    protected void setup() {
        Product p = new Product();
        p.price = 1000;
        p.name = "keyboard";

        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
        m.addReceiver(new AID("Buyer", AID.ISLOCALNAME));

        try {
            m.setContentObject(p);
            m.setLanguage("JavaSerialization");
            send(m);
            System.out.println("Product sent: " + p.name + " / " + p.price);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}