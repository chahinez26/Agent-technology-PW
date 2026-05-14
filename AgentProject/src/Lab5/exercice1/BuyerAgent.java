package Lab5.exercice1;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
 
public class BuyerAgent extends Agent {
 
    private Object[] cars = null;
 
    protected void setup() {
        System.out.println("Starting of agent :" + this.getAID().getName());
        try {
            // Creation of the Buyer Agent description
            DFAgentDescription Afd = new DFAgentDescription();
            Afd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setType("car-buying");
            sd.setName("buyer-service");
            Afd.addServices(sd);
            // Register the agent's description in DF
            DFService.register(this, Afd);
            System.out.println(getLocalName() + " Registration in the DF directory");
        } catch (FIPAException e) {
            e.printStackTrace();
        }
 
        /* Preparing message to send to seller agent.
           This message contains request for products list. */
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        // Filling in the message content
        msg.setContent("Request for car lists");
        // Specify the agents receiving the message; in this case, it is the Seller agent.
        msg.addReceiver(new AID("seller1", AID.ISLOCALNAME));
        // Send the message to the seller agent
        send(msg);
        System.out.println(getLocalName() + " --> Request for car lists");
 
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                // Waiting for message (from the Seller Agent)
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if (msg != null) {
                    // The Buyer agent responds to the Seller agent bye
                    try {
                        ACLMessage reply = msg.createReply();
                        // The response content is <Bye>
                        reply.setContent("Bye");
                        // Send the response to the Sales Agent
                        send(reply);
                        // Displaying the answer
                        cars = (Object[]) msg.getContentObject();
                        System.out.println("the list of products is : ");
                        for (int i = 0; i < cars.length; i++) {
                            System.out.println(cars[i]);
                        }
                        System.out.println(getAID() + " sent Bye");
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                    // Buyer agent termination
                    doDelete();
                } else {
                    // The behavior is blocked while the message has not yet arrived.
                    block();
                }
            }
        });
    }
 
    protected void takeDown() {
        // Deletion of the [Buyer] agent from the DF
        try {
            DFService.deregister(this);
            System.out.println(getLocalName() + " cancellation of registration with DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}