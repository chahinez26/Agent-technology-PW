package Lab5.exercice1;


import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
 
import java.io.IOException;
 
public class SellerAgent extends Agent {
 
    protected void setup() {
        System.out.println("Starting of agent:" + this.getAID().getName());
        try {
            // Creating the description of the agent [Seller]
            DFAgentDescription Afd = new DFAgentDescription();
            Afd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setType("car-selling");
            sd.setName("seller-service");
            Afd.addServices(sd);
            // Registering the agent's description in DF (Directory Facilitator)
            DFService.register(this, Afd);
            System.out.println(getLocalName() + " Agent registered in DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }
 
        // adding agent behavior
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                // Awaiting message (from the Buyer agent)
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if (msg != null) {
                    if (msg.getContent().equalsIgnoreCase("Request for car lists")) {
                        System.out.println("receipt of the customer's request ");
                        ACLMessage reply = msg.createReply();
                        Object[] cars = {
                            "PORCHE", "LAMBORGHINI", "MASERATI",
                            "CHEVROLET", "LAGUNA", "ROMEO"
                        };
                        try {
                             reply.setContentObject(cars);
                        } catch (IOException e) {e.printStackTrace();}
                        // Send the response to the Buyer Agent
                        myAgent.send(reply);
                        System.out.println("Sending the list of cars to the customer ");
                    }
                    // the seller receives message Bye
                    else if (msg.getContent().equalsIgnoreCase("Bye")) {
                        ACLMessage reply = msg.createReply();
                        reply.setContent("Bye");
                        // the seller sends Bye
                        myAgent.send(reply);
                        System.out.println(getLocalName() + " replied");
                        // Seller agent termination
                        myAgent.doDelete();
                    }
                } else {
                    // waiting for the customer's message
                    block();
                }
            }
        });
    }
 
    protected void takeDown() {
        // Removal of the Seller agent from the DF
        try {
            DFService.deregister(this);
            System.out.println(getLocalName() + " cancellation of registration with DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}