package projet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class backward_chain {

    public static ArrayList<String> reste(ArrayList<String> a) {
        ArrayList<String> r = new ArrayList<String>();
        for (int i = 1; i < a.size(); i++) {
            r.add(a.get(i));
        }
        return r;
    }

    // Version sans trace (usage terminal)
    public static boolean back_ch(Rule[] bdr, ArrayList<String> bdp, ArrayList<String> fp) {
        return back_ch(bdr, bdp, fp, null);
    }

    // Version avec trace (pour l'interface) — remplit trace avec "R0", "R1", ...
    public static boolean back_ch(Rule[] bdr, ArrayList<String> bdp, ArrayList<String> fp, List<String> trace) {
        boolean res;
        if (fp.isEmpty()) {
            res = true;
        } else {
            if (takegoal(bdr, bdp, fp.get(0), trace)) {
                bdp.add(fp.get(0));
                res = back_ch(bdr, bdp, reste(fp), trace);
            } else {
                res = false;
            }
        }
        return res;
    }

    public static boolean takegoal(Rule[] bdr, ArrayList<String> bdp, String but, List<String> trace) {
        boolean res;
        if (bdp.contains(but)) {
            res = true;
        } else {
            res = false;
            ArrayList<Rule> RA = new ArrayList<Rule>();
            for (int i = 0; i < bdr.length; i++) {
                if (bdr[i].conclusions.contains(but)) {
                    RA.add(bdr[i]);
                }
            }
            while (!RA.isEmpty() && res != true) {
                Rule r = RA.get(0);
                RA.remove(0);
                // Ajouter la règle à la trace
                if (trace != null) trace.add("R" + r.name);
                res = back_ch(bdr, bdp, r.premises, trace);
            }
        }
        return res;
    }

    public static void main(String[] args) {
        Rule[] bdr = new Rule[9];

        ArrayList<String> p = new ArrayList<>(Arrays.asList("A", "B"));
        ArrayList<String> c = new ArrayList<>(Arrays.asList("F"));
        bdr[0] = new Rule(0, p, c);

        p = new ArrayList<>(Arrays.asList("F", "H"));
        c = new ArrayList<>(Arrays.asList("I"));
        bdr[1] = new Rule(1, p, c);

        p = new ArrayList<>(Arrays.asList("D", "H", "G"));
        c = new ArrayList<>(Arrays.asList("A"));
        bdr[2] = new Rule(2, p, c);

        p = new ArrayList<>(Arrays.asList("O", "G"));
        c = new ArrayList<>(Arrays.asList("H"));
        bdr[3] = new Rule(3, p, c);

        p = new ArrayList<>(Arrays.asList("E", "H"));
        c = new ArrayList<>(Arrays.asList("B"));
        bdr[4] = new Rule(4, p, c);

        p = new ArrayList<>(Arrays.asList("G", "A"));
        c = new ArrayList<>(Arrays.asList("B"));
        bdr[5] = new Rule(5, p, c);

        p = new ArrayList<>(Arrays.asList("G", "H"));
        c = new ArrayList<>(Arrays.asList("P"));
        bdr[6] = new Rule(6, p, c);

        p = new ArrayList<>(Arrays.asList("G", "H"));
        c = new ArrayList<>(Arrays.asList("O"));
        bdr[7] = new Rule(7, p, c);

        p = new ArrayList<>(Arrays.asList("D", "O", "G"));
        c = new ArrayList<>(Arrays.asList("J"));
        bdr[8] = new Rule(8, p, c);

        ArrayList<String> fp = new ArrayList<String>(Arrays.asList("I"));
        ArrayList<String> BDP = new ArrayList<String>(Arrays.asList("D", "O", "G"));

        boolean test = back_ch(bdr, BDP, fp);
        System.out.println(test);
    }
}