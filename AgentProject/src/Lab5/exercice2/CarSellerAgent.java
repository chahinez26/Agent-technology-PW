package Lab5.exercice2;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;

/**

 */
public class CarSellerAgent extends Agent {

    protected void setup() {
        System.out.println("Starting of agent:" + this.getAID().getName());

        // ─────────────────────────────────────────────────────────
        //  ÉTAPE 1 : Enregistrement dans le DF
        // ─────────────────────────────────────────────────────────
     
        try {
            DFAgentDescription Afd = new DFAgentDescription();
            Afd.setName(getAID());                       // Notre identité dans le DF

            ServiceDescription sd = new ServiceDescription();
            sd.setType("car-selling");                   // Clé de recherche utilisée par le CarBuyer
            sd.setName("car-seller-service");            // Nom descriptif du service
            Afd.addServices(sd);

            DFService.register(this, Afd);               // Publication dans l'annuaire DF
            System.out.println(getLocalName() + " Agent registered in DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // ─────────────────────────────────────────────────────────
        //  ÉTAPE 2 : Mise en place du comportement cyclique
        // ─────────────────────────────────────────────────────────

        addBehaviour(new CyclicBehaviour() {
            public void action() {

                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if (msg != null) {
                    
                    if (msg.getContent().equalsIgnoreCase("Request for car lists")) {
                        System.out.println("receipt of the customer's request ");

                        ACLMessage reply = msg.createReply(); // Création du message de réponse.
                        Object[] cars = {
                            "PORCHE", "LAMBORGHINI", "MASERATI",
                            "CHEVROLET", "LAGUNA", "ROMEO"
                        };
                        try {
                            reply.setContentObject(cars);  // Sérialisation Java de l'objet
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        myAgent.send(reply);
                        System.out.println("Sending the list of cars to the customer ");

                    // ─────────────────────────────────────────────
                    //  CAS 2 : Réception du message "Bye"
                    // ─────────────────────────────────────────────
                    // L'acheteur nous signale qu'il a bien reçu la liste
                    // et qu'il met fin à la conversation.
                    // On répond "Bye" par politesse, puis on se termine.
                    // ─────────────────────────────────────────────
                    } else if (msg.getContent().equalsIgnoreCase("Bye")) {
                        ACLMessage reply = msg.createReply();
                        reply.setContent("Bye");
                        myAgent.send(reply);
                        System.out.println(getLocalName() + " replied");

                        // doDelete() déclenche l'appel à takeDown()
                        // qui s'occupera du désenregistrement du DF.
                        myAgent.doDelete();
                    }

                } else {
                    // ─────────────────────────────────────────────
                    //  Aucun message disponible → on se bloque
                    // ─────────────────────────────────────────────
                    // block() suspend ce behaviour jusqu'à l'arrivée
                    // du prochain message. C'est une attente passive
                    // qui ne consomme pas de ressources CPU,
                    // contrairement à une boucle active (busy wait).
                    // ─────────────────────────────────────────────
                    block();
                }
            }
        });
    }

    protected void takeDown() {
        // ─────────────────────────────────────────────────────────
        //  Désenregistrement du DF à la fin de vie de l'agent
        // ─────────────────────────────────────────────────────────
        // Il est important de se désenregistrer du DF pour que
        // les autres agents ne trouvent plus ce service dans l'annuaire.
        // Cela maintient la cohérence du DF.
        // ─────────────────────────────────────────────────────────
        try {
            DFService.deregister(this);
            System.out.println(getLocalName() + " cancellation of registration with DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}