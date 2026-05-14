package AgentProject;

import jade.core.Runtime;
import jade.core.Agent;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class exercice4 extends Agent {

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if(args != null) {
            for(Object arg : args) {
                System.out.println(
                        getLocalName() + " : " + arg
                );
            }   }}

    public static void main(String[] args) {
        try {

            Runtime rt = Runtime.instance();
            ProfileImpl p = new ProfileImpl("localhost", 1104,"JADE");
            p.setParameter(ProfileImpl.GUI, "true");
            ContainerController mc = rt.createMainContainer(p);
            Object[] args1 = {"je", "suis", "Buyer1"};

            Object[] args2 = {"je", "suis", "Buyer2"};

            AgentController ag1 = mc.createNewAgent("Buyer1","AgentProject.exercice4",args1 );
            ag1.start();
            AgentController ag2 = mc.createNewAgent("Buyer2","AgentProject.exercice4",args2 );
            ag2.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}