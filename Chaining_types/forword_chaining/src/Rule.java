package projet;
import java.util.ArrayList;

public class Rule {

    boolean state;
    ArrayList<String> premises;   // p
    ArrayList<String> conclusions; // c
    int name;

    public Rule(int i, ArrayList<String> p, ArrayList<String> c) {
        this.name = i;
        this.state = true;
        this.premises = p;
        this.conclusions = c;
    }
}
