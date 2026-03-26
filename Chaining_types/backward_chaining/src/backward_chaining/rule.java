package backward_chaining;
import java.util.ArrayList;
// classe pour 
public class rule {
    boolean state;
    ArrayList<String> p;
    ArrayList<String> c;
    int name;

    public rule (int i, ArrayList<String> a1, ArrayList<String> a2) {
        name = i;
        state = true;
        p = a1;
        c = a2;
    }
}