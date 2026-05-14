package Lab3;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class B2 extends Agent {
    protected void setup() {
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage m = receive();
                if (m != null) {
                    try {
                        Product p = (Product) m.getContentObject();
                        System.out.println("I am " + getLocalName() +
                            " I received the product: " + p.name +
                            " with the price: " + p.price);
                    } catch (UnreadableException e) {
                        System.err.println(getLocalName() +
                            " caught exception: " + e.getMessage());
                    }
                } else {
                    block(); 
                }
            }
        });
    }
}