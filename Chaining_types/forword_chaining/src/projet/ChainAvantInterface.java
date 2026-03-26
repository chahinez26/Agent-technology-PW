package projet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChainAvantInterface extends JFrame {

    private final JTextArea factsArea;
    private final JTextField goalField;
    private final JTextArea resultArea;
    private final JLabel statusLabel;
    private final JComboBox<String> chainingBox;
    private Rule[] ruleBase;

    public ChainAvantInterface() {
        setTitle("Inference Engine - Chaining");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 700);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(new EmptyBorder(15, 15, 15, 15));
        main.setBackground(new Color(0xF5F5F5));

        // --- Input Panel ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(0xF5F5F5));
        TitledBorder inputBorder = new TitledBorder(" Input Data ");
        inputBorder.setBorder(BorderFactory.createLineBorder(new Color(0x1976D2), 2));
        inputBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        inputBorder.setTitleColor(new Color(0x1976D2));
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            inputBorder,
            new EmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // --- ComboBox: chaining type ---
        JLabel chainingLabel = new JLabel("Chaining type:");
        chainingLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(chainingLabel, gbc);

        chainingBox = new JComboBox<>(new String[]{"Forward Chaining", "Backward Chaining"});
        chainingBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chainingBox.setBackground(Color.WHITE);
        chainingBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chainingBox.addActionListener(e -> updateUI());
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        inputPanel.add(chainingBox, gbc);

        // --- Initial Facts ---
        JLabel factsLabel = new JLabel("Initial fact base:");
        factsLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(factsLabel, gbc);
        /*
        JLabel factsHint = new JLabel("(comma-separated, e.g: D, O, G)");
        factsHint.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        factsHint.setForeground(new Color(0x666666));
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(factsHint, gbc);
        */
        factsArea = new JTextArea(3, 40);
        factsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        factsArea.setLineWrap(true);
        factsArea.setWrapStyleWord(true);
        factsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x90A4AE)),
            new EmptyBorder(6, 6, 6, 6)
        ));
        JScrollPane factsScroll = new JScrollPane(factsArea);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        inputPanel.add(factsScroll, gbc);

        // --- Goal ---
        JLabel goalLabel = new JLabel("Goal to reach:");
        goalLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(goalLabel, gbc);

        /*
        JLabel goalHint = new JLabel("(single symbol, e.g: I)");
        goalHint.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        goalHint.setForeground(new Color(0x666666));
        gbc.gridx = 1; gbc.gridy = 3;
        inputPanel.add(goalHint, gbc);
        */
        goalField = new JTextField(20);
        goalField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        goalField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x90A4AE)),
            new EmptyBorder(6, 6, 6, 6)
        ));
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        inputPanel.add(goalField, gbc);

        // --- Run Button ---
        JButton runBtn = new JButton("▶ Run Chaining");
        runBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        runBtn.setBackground(new Color(0x1976D2));
        runBtn.setForeground(Color.WHITE);
        runBtn.setFocusPainted(false);
        runBtn.setBorderPainted(false);
        runBtn.setPreferredSize(new Dimension(250, 35));
        runBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        runBtn.addActionListener(e -> runChaining());
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        inputPanel.add(runBtn, gbc);

        // --- Result Area ---
        resultArea = new JTextArea(10, 35);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        resultArea.setBackground(new Color(0xFAFAFA));
        resultArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        TitledBorder resultBorder = new TitledBorder(" Result ");
        resultBorder.setBorder(BorderFactory.createLineBorder(new Color(0x90A4AE), 2));
        resultBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        resultBorder.setTitleColor(new Color(0x455A64));
        resultScroll.setBorder(BorderFactory.createCompoundBorder(
            resultBorder,
            new EmptyBorder(3, 3, 3, 3)
        ));

        statusLabel = new JLabel("Ready to run chaining");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(0x666666));

        // --- Layout Assembly ---
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBackground(new Color(0xF5F5F5));
        rightPanel.add(inputPanel, BorderLayout.NORTH);
        rightPanel.add(resultScroll, BorderLayout.CENTER);
        rightPanel.add(statusLabel, BorderLayout.SOUTH);

        main.add(rightPanel, BorderLayout.CENTER);

        setContentPane(main);
        initRules();
        fillDefaults();
    }

    private void updateUI() {
        String choice = (String) chainingBox.getSelectedItem();
        if ("Forward Chaining".equals(choice)) {
            setTitle("Forward Chaining - Interface");
            statusLabel.setText("Ready to run forward chaining");
        } else {
            setTitle("Backward Chaining - Interface");
            statusLabel.setText("Ready to run backward chaining");
        }
        statusLabel.setForeground(new Color(0x666666));
        resultArea.setText("");
    }

    private void initRules() {
        Rule[] bdr = new Rule[9];
        ArrayList<String> p, c;

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

        this.ruleBase = bdr;
    }

    private void fillDefaults() {
        factsArea.setText("D, O, G");
        goalField.setText("I");
    }

    private void runChaining() {
        String factsText = factsArea.getText().trim();
        String goalText = goalField.getText().trim();

        if (factsText.isEmpty()) {
            statusLabel.setText("⚠ Please enter the initial fact base.");
            statusLabel.setForeground(new Color(0xC62828));
            resultArea.setText("");
            return;
        }

        if (goalText.isEmpty()) {
            statusLabel.setText("⚠ Please enter a goal.");
            statusLabel.setForeground(new Color(0xC62828));
            resultArea.setText("");
            return;
        }

        String[] parts = factsText.split("[,;\\s]+");
        ArrayList<String> facts = new ArrayList<>();
        for (String s : parts) {
            String x = s.trim();
            if (!x.isEmpty()) facts.add(x);
        }

        if (facts.isEmpty()) {
            statusLabel.setText("⚠ No valid facts found.");
            statusLabel.setForeground(new Color(0xC62828));
            resultArea.setText("");
            return;
        }

        String choice = (String) chainingBox.getSelectedItem();
        boolean result;
        ArrayList<String> factsCopy = new ArrayList<>(facts);
        List<String> trace = new ArrayList<>();

        resultArea.setText("");

        if ("Forward Chaining".equals(choice)) {
            // --- Forward Chaining ---
            ArrayList<String> goals = new ArrayList<>();
            goals.add(goalText.trim());

            result = ChainAvant.chain(ruleBase, factsCopy, goals, trace);

            resultArea.append("═══════════════════════════════════════\n");
            resultArea.append("  FORWARD CHAINING\n");
            resultArea.append("═══════════════════════════════════════\n\n");
            resultArea.append("Initial fact base : " + facts + "\n");
            resultArea.append("Goal              : " + goals + "\n\n");

            if (!trace.isEmpty()) {
                resultArea.append("Rules applied (" + trace.size() + ") : ");
                resultArea.append(String.join(" → ", trace) + "\n\n");
            } else {
                resultArea.append("No rules applied.\n\n");
            }

            resultArea.append("Final fact base   : " + factsCopy + "\n\n");

        } else {
            // --- Backward Chaining ---
            ArrayList<String> fp = new ArrayList<>();
            fp.add(goalText.trim());

            result = backward_chain.back_ch(ruleBase, factsCopy, fp, trace);

            resultArea.append("═══════════════════════════════════════\n");
            resultArea.append("  BACKWARD CHAINING\n");
            resultArea.append("═══════════════════════════════════════\n\n");
            resultArea.append("Initial fact base : " + facts + "\n");
            resultArea.append("Goal              : " + goalText + "\n\n");

            if (!trace.isEmpty()) {
                resultArea.append("Rules applied (" + trace.size() + ") : ");
                resultArea.append(String.join(" → ", trace) + "\n\n");
            } else {
                resultArea.append("No rules applied.\n\n");
            }

            resultArea.append("Final fact base   : " + factsCopy + "\n\n");
        }

        resultArea.append("═══════════════════════════════════════\n");
        resultArea.append("Goal reached? " + (result ? "✓ YES" : "✗ NO") + "\n");
        resultArea.append("═══════════════════════════════════════\n");

        if (result) {
            statusLabel.setText("✓ Goal reached successfully!");
            statusLabel.setForeground(new Color(0x2E7D32));
        } else {
            statusLabel.setText("✗ Goal not reached.");
            statusLabel.setForeground(new Color(0xC62828));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            ChainAvantInterface f = new ChainAvantInterface();
            f.setVisible(true);
        });
    }
}