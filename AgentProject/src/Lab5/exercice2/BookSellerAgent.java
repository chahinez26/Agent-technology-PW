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
 * ============================================================
 *  BookSellerAgent — Agent Vendeur de Livres
 * ============================================================
 *
 *  Rôle DANS L'EXERCICE :
 *  Cet agent est intentionnellement présent pour démontrer
 *  que le mécanisme de filtrage du DF fonctionne correctement.
 *
 *  Il s'enregistre dans le DF avec le type "book-selling",
 *  qui est DIFFÉRENT de "car-selling".
 *  Le CarBuyerAgent recherche uniquement "car-selling",
 *  donc il ne trouvera JAMAIS cet agent via le DF.
 *
 *  Analogie : Dans un annuaire (pages jaunes), si vous
 *  cherchez "garagiste", vous n'obtenez pas les "librairies",
 *  même si elles sont dans le même annuaire.
 *
 *  Logique générale :
 *  1. S'enregistre dans le DF avec le type "book-selling".
 *  2. Attend des messages (mais n'en recevra pas dans cet exercice).
 *  3. Resterait actif indéfiniment, sauf si un acheteur
 *     de livres lui envoyait un message.
 * ============================================================
 */
public class BookSellerAgent extends Agent {

    protected void setup() {
        System.out.println("Starting of agent:" + this.getAID().getName());

        // ─────────────────────────────────────────────────────────
        //  ÉTAPE 1 : Enregistrement dans le DF
        // ─────────────────────────────────────────────────────────
        // Cet agent s'enregistre dans le DF avec le type "book-selling".
        //
        // POINT CLÉ : Le type "book-selling" est DIFFÉRENT de "car-selling".
        // Lorsque le CarBuyerAgent effectuera sa recherche dans le DF
        // avec sd.setType("car-selling"), cette entrée ne sera PAS
        // retournée par DFService.search().
        //
        // Le DF agit comme un filtre : seuls les agents dont le type
        // de service correspond exactement à la requête sont retournés.
        // ─────────────────────────────────────────────────────────
        try {
            DFAgentDescription Afd = new DFAgentDescription();
            Afd.setName(getAID());

            ServiceDescription sd = new ServiceDescription();
            sd.setType("book-selling");        // Type DIFFÉRENT → ignoré par le CarBuyer
            sd.setName("book-seller-service");
            Afd.addServices(sd);

            DFService.register(this, Afd);     // Présent dans le DF mais non sollicité
            System.out.println(getLocalName() + " Agent registered in DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // ─────────────────────────────────────────────────────────
        //  ÉTAPE 2 : Comportement cyclique d'attente
        // ─────────────────────────────────────────────────────────
        // Cet agent attend des messages, mais dans le cadre de
        // cet exercice, il n'en recevra aucun de la part du CarBuyer,
        // car ce dernier ne l'aura pas trouvé dans le DF.
        //
        // Il resterait fonctionnel si un futur agent acheteur de livres
        // (BookBuyerAgent) cherchait "book-selling" dans le DF.
        // ─────────────────────────────────────────────────────────
        addBehaviour(new CyclicBehaviour() {
            public void action() {

                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));

                if (msg != null) {
                    // ─────────────────────────────────────────────
                    //  Réception d'une requête de liste de livres
                    // ─────────────────────────────────────────────
                    // Ce cas ne sera pas atteint dans cet exercice,
                    // mais il illustre comment cet agent fonctionnerait
                    // si un acheteur de livres le contactait.
                    // ─────────────────────────────────────────────
                    if (msg.getContent().equalsIgnoreCase("Request for book lists")) {
                        System.out.println("receipt of the customer's request ");

                        ACLMessage reply = msg.createReply();
                        Object[] books = {
                            "Design Patterns",
                            "Clean Code",
                            "The Pragmatic Programmer",
                            "Introduction to Algorithms",
                            "Artificial Intelligence: A Modern Approach"
                        };
                        try {
                            reply.setContentObject(books);   // Sérialisation de la liste de livres
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        myAgent.send(reply);
                        System.out.println("Sending the list of books to the customer ");

                    } else if (msg.getContent().equalsIgnoreCase("Bye")) {
                        // Fin de conversation avec un éventuel acheteur de livres
                        ACLMessage reply = msg.createReply();
                        reply.setContent("Bye");
                        myAgent.send(reply);
                        System.out.println(getLocalName() + " replied");
                        myAgent.doDelete();
                    }

                } else {
                    // ─────────────────────────────────────────────
                    //  Attente passive d'un message
                    // ─────────────────────────────────────────────
                    // Cet agent restera bloqué ici tout au long de
                    // l'exercice, car aucun agent ne le contactera.
                    // Il prouve que le DF n'a retourné que le CarSeller.
                    // ─────────────────────────────────────────────
                    block();
                }
            }
        });
    }

    protected void takeDown() {
        // Désenregistrement du DF à la fin de vie de l'agent
        try {
            DFService.deregister(this);
            System.out.println(getLocalName() + " cancellation of registration with DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}