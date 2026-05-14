package Lab5.exercice2;

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


public class CarBuyerAgent extends Agent {

    private Object[] cars = null;

    protected void setup() {
        System.out.println("Starting of agent :" + this.getAID().getName());

        // ─────────────────────────────────────────────────────────
        //  ÉTAPE 1 : Enregistrement dans le DF
        // ─────────────────────────────────────────────────────────
        // Même les agents acheteurs peuvent s'enregistrer dans le DF.
        // Cela permet à d'éventuels autres agents de savoir qu'un
        // acheteur de voitures est actif sur la plateforme.
        // ─────────────────────────────────────────────────────────
        try {
            DFAgentDescription Afd = new DFAgentDescription();
            Afd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setType("car-buying");
            sd.setName("buyer-service");
            Afd.addServices(sd);
            DFService.register(this, Afd);
            System.out.println(getLocalName() + " Registration in the DF directory");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // ─────────────────────────────────────────────────────────
        //  ÉTAPE 2 : Recherche du vendeur de voitures dans le DF
        // ─────────────────────────────────────────────────────────
        // On interroge le DF pour trouver un agent qui offre
        // le service de type "car-selling".
        // ─────────────────────────────────────────────────────────
        AID carSellerAID = searchCarSellerInDF();

        if (carSellerAID == null) {
            // Si aucun vendeur de voitures n'est trouvé dans le DF
            System.out.println(getLocalName() + " No car seller found in DF. Terminating.");
            doDelete();
            return;
        }
        System.out.println(getLocalName() + " Found car seller in DF : " + carSellerAID.getLocalName());

        // ─────────────────────────────────────────────────────────
        //  ÉTAPE 3 : Envoi de la requête au vendeur trouvé
        // ─────────────────────────────────────────────────────────
        // Maintenant qu'on connaît l'AID du CarSellerAgent (obtenu
        // dynamiquement via le DF), on lui envoie notre requête.
        //
        // Contrairement à l'exercice 1 où on écrivait :
        //   msg.addReceiver(new AID("seller1", AID.ISLOCALNAME))
        // Ici on utilise l'AID récupéré du DF :
        //   msg.addReceiver(carSellerAID)
        // ─────────────────────────────────────────────────────────
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("Request for car lists");
        msg.addReceiver(carSellerAID);   // AID obtenu dynamiquement du DF
        send(msg);
        System.out.println(getLocalName() + " --> Request for car lists");

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {

                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));

                if (msg != null) {
                    try {
                        ACLMessage reply = msg.createReply();
                        reply.setContent("Bye");
                        send(reply);

                        cars = (Object[]) msg.getContentObject();
                        System.out.println("the list of products is : ");
                        for (int i = 0; i < cars.length; i++) {
                            System.out.println(cars[i]);
                        }
                        System.out.println(getAID() + " sent Bye");

                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                    // Mission accomplie → on se termine.
                    doDelete();

                } else {
                    block();
                }
            }
        });
    }

 
    private AID searchCarSellerInDF() {
        // Template de recherche : on ne précise que le type de service.
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("car-selling");    // Critère de filtrage
        template.addServices(sd);

        try {
            // Résultats : tableau de DFAgentDescription
            DFAgentDescription[] results = DFService.search(this, template);
            if (results.length > 0) {
                // On extrait l'AID de la première correspondance trouvée
                return results[0].getName();
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        return null;  // Aucun vendeur de voitures trouvé dans le DF
    }

    protected void takeDown() {
        // Désenregistrement propre du DF à la fin de vie de l'agent
        try {
            DFService.deregister(this);
            System.out.println(getLocalName() + " cancellation of registration with DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}