package Lab4;

import jade.core.Agent;
import jade.core.Location;

public class MobileAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("[" + getLocalName() + "] Setup — je suis dans : " 
            + here().getName());
        System.out.println("[" + getLocalName() + "] AVANT migration — prêt à migrer.");
        // L'agent attend la migration via la GUI JADE (pas de doMove ici)
    }

    @Override
    protected void beforeMove() {
        System.out.println("[" + getLocalName() + "] PENDANT migration — je quitte : " 
            + here().getName());
    }

    @Override
    protected void afterMove() {
        System.out.println("[" + getLocalName() + "] APRÈS migration — je suis arrivé dans : " 
            + here().getName());
    }
}