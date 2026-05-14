package Lab3;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.AID;
import jade.core.messaging.TopicManagementHelper;

public class exo41 extends Agent {
	    protected void setup() {
	        try {
	            // S'enregistrer au topic "JADE"
	            TopicManagementHelper topicHelper =
	                (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
	            final AID topic = topicHelper.createTopic("JADE"); 
	            topicHelper.register(topic);

	            // Collecter les messages sur le topic "JADE"
	            addBehaviour(new CyclicBehaviour(this) {
	                public void action() {
	                    ACLMessage msg = myAgent.receive(MessageTemplate.MatchTopic(topic));
	                    if (msg != null) {
	                        System.out.println("Agent " + myAgent.getLocalName() +
	                            ": Message about topic " + topic.getLocalName() +
	                            " received. Content is " + msg.getContent());
	                    } else {
	                        block();
	                    }
	                }
	            });
	        } catch (Exception e) {
	            System.err.println("Agent " + getLocalName() +
	                ": ERROR registering to topic \"JADE\"");
	            e.printStackTrace();
	        }
	    }
	}
