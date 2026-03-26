package projet;
import java.util.ArrayList;
import java.util.Arrays;

public class ChainAvant {

    // Fonction Chainage Avant
    public static boolean chain(Rule[] rules, ArrayList<String> facts, ArrayList<String> goals) {

        boolean changed = true;

        while (changed) {
            changed = false;

            for (Rule r : rules) {

                // Vérifier si toutes les prémisses sont dans la base de faits
                if (facts.containsAll(r.premises)) {

                    for (String conclusion : r.conclusions) {

                        if (!facts.contains(conclusion)) {
                            facts.add(conclusion);
                            changed = true;
                            System.out.println("Règle appliquée: R" + r.name);
                        }
                    }
                }
            }
        }

        // Vérifier si le but est atteint
        return facts.containsAll(goals);
    }

    /** Chainage avant avec trace (pour l'interface). Remplit trace avec "R0", "R1", ... */
    public static boolean chain(Rule[] rules, ArrayList<String> facts, ArrayList<String> goals, java.util.List<String> trace) {
        if (trace != null) trace.clear();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Rule r : rules) {
                if (facts.containsAll(r.premises)) {
                    for (String conclusion : r.conclusions) {
                        if (!facts.contains(conclusion)) {
                            facts.add(conclusion);
                            changed = true;
                            if (trace != null) trace.add("R" + r.name);
                        }
                    }
                }
            }
        }
        return facts.containsAll(goals);
    }

    public static void main(String[] args) {

        Rule[] bdr = new Rule[9];

        ArrayList<String> p;
        ArrayList<String> c;

        p = new ArrayList<>(Arrays.asList("A", "B"));
        c = new ArrayList<>(Arrays.asList("F"));
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

        // Base de faits initiale
        ArrayList<String> BDP = new ArrayList<>(Arrays.asList("D", "O", "G"));

        // But
        ArrayList<String> fp = new ArrayList<>(Arrays.asList("I"));

        boolean result = chain(bdr, BDP, fp);

        System.out.println("But atteint ? " + result);
        System.out.println("Base finale: " + BDP);
    }
}