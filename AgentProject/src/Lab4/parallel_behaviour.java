package Lab4;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;

public class parallel_behaviour extends Agent {

    @Override
    protected void setup() {
        System.out.println("I, Agent " + getLocalName() + ", my address is " + getAID());
        System.out.println("I execute several behaviors in parallel");

        ParallelBehaviour paraB = new ParallelBehaviour();

        paraB.addSubBehaviour(langhelloBehaviour("عليكم السلام"));
        paraB.addSubBehaviour(langhelloBehaviour("bonjour"));
        paraB.addSubBehaviour(langhelloBehaviour("hallo"));
        paraB.addSubBehaviour(langhelloBehaviour("buongiorno"));
        paraB.addSubBehaviour(langhelloBehaviour("buenos dias"));
        paraB.addSubBehaviour(langhelloBehaviour("Olá"));
        paraB.addSubBehaviour(langhelloBehaviour("saluton"));

        addBehaviour(paraB);
    }

    private Behaviour langhelloBehaviour(String msg) {
        return new Behaviour(this) {
            int i = 0;

            @Override
            public void action() {
                System.out.printf("%s -> %s (%d/3)%n", getLocalName(), msg, (i + 1));
                i++;
            }

            @Override
            public boolean done() {
                return i >= 3;
            }
        };
    }
}