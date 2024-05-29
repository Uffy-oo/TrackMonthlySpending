import main.SpendingTracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class SpendingTrackerGUI extends JFrame {
    private JTextField emailField;
    private JTextField limitField;
    private JTextField expenseAmountField;
    private JTextField expenseDescriptionField;
    private SpendingTracker expenseTracker;
    private int userId;

    public SpendingTrackerGUI() {
        expenseTracker = new SpendingTracker();

        setTitle("Expense Tracker");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Monthly Limit:"));
        limitField = new JTextField();
        panel.add(limitField);

        JButton setLimitButton = new JButton("Set Limit");
        setLimitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setMonthlyLimit();
            }
        });
        panel.add(setLimitButton);

        panel.add(new JLabel("Expense Amount:"));
        expenseAmountField = new JTextField();
        panel.add(expenseAmountField);

        panel.add(new JLabel("Expense Description:"));
        expenseDescriptionField = new JTextField();
        panel.add(expenseDescriptionField);

        JButton addExpenseButton = new JButton("Add Expense");
        addExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addExpense();
            }
        });
        panel.add(addExpenseButton);

        add(panel);
    }

    private void setMonthlyLimit() {
        String email = emailField.getText();
        double limit = Double.parseDouble(limitField.getText());

        try {
            userId = expenseTracker.setUserMonthlyLimit(email, limit);
            JOptionPane.showMessageDialog(this, "Monthly limit set successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error setting monthly limit.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addExpense() {
        double amount = Double.parseDouble(expenseAmountField.getText());
        String description = expenseDescriptionField.getText();

        try {
            expenseTracker.addExpense(userId, amount, description);
            JOptionPane.showMessageDialog(this, "Expense added successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding expense.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SpendingTrackerGUI().setVisible(true);
            }
        });
    }
}
