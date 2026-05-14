package Lab4;

import jade.core.Agent;
import jade.core.Location;
import jade.core.behaviours.*;

public class AgentBehaviour extends Agent {

    private int counter;

    @Override
    protected void setup() {
        System.out.println("Démarrage de l'agent : " + getAID().getName());
/*
        addBehaviour(new Behaviour() {
            private int count = 0;

            @Override
            public void action() {
                count++;
                System.out.println("[Behaviour] Exécution #" + count);
            }

            @Override
            public boolean done() {
                return count >= 3; // s'arrête après 3 fois
            }
        });
        
       
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("[OneShotBehaviour] Exécuté une seule fois.");
            }
        });
        
        addBehaviour(new CyclicBehaviour() {
            private int count = 0;

            @Override
            public void action() {
                count++;
                System.out.println("[CyclicBehaviour] Cycle #" + count);
                if (count >= 3) removeBehaviour(this); // arrêt manuel
            }
        });
       
        addBehaviour(new WakerBehaviour(this, 6000) {
            @Override
            protected void onWake() {
                System.out.println("[WakerBehaviour] Réveil après 6s → agent terminé.");
                doDelete();
            }
        });
 */
        addBehaviour(new TickerBehaviour(this, 2000) {
            @Override
            protected void onTick() {
                counter++;
                System.out.println("[TickerBehaviour] Tick #" + counter);
                if (counter >= 3) removeBehaviour(this); // arrêt manuel
            }
        });
        
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent terminé.");
    }

    // Correction du DoMove (le "......" du sujet)
    public void DoMove(Location l) {
        System.out.println("Migration de l'agent vers : " + l.getName());
        doMove(l);
    }
}