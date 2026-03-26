package AgentProject;
import jade.core.Agent;

public class passing_arguments extends Agent {
    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null) {
            for (Object arg : args) {
                System.out.println(getLocalName() + " received: " + arg);
            }
        }
    }
}