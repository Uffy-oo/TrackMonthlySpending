package main;

import dao.DatabaseConnection;
import dao.SpendingDAO;
import service.EmailNotifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SpendingTracker {
    private final SpendingDAO expenseDAO = new SpendingDAO();
    private final EmailNotifier emailNotifier = new EmailNotifier();

    public int setUserMonthlyLimit(String email, double limit) throws SQLException {
        String query = "INSERT INTO users (email, monthly_limit) VALUES (?, ?) ON DUPLICATE KEY UPDATE monthly_limit = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, email);
            statement.setDouble(2, limit);
            statement.setDouble(3, limit);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    public void addExpense(int userId, double amount, String description) throws SQLException {
        expenseDAO.addExpense(userId, amount, description);
        checkAndNotify(userId);
    }

    private void checkAndNotify(int userId) throws SQLException {
        double monthlyLimit = getUserMonthlyLimit(userId);
        double totalExpenses = expenseDAO.getTotalExpenses(userId, getCurrentMonth(), getCurrentYear());

        if (totalExpenses > monthlyLimit) {
            emailNotifier.sendEmail(getUserEmail(userId), "Monthly Limit Exceeded",
                    "You have exceeded your monthly spending limit. Your total spending is: " + totalExpenses);
        } else if (totalExpenses > monthlyLimit / 2) {
            emailNotifier.sendEmail(getUserEmail(userId), "Monthly Limit Half Reached",
                    "You have reached half of your monthly spending limit. Your total spending is: " + totalExpenses);
        }
    }

    private double getUserMonthlyLimit(int userId) throws SQLException {
        String query = "SELECT monthly_limit FROM users WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble(1);
                } else {
                    throw new SQLException("User not found.");
                }
            }
        }
    }

    private String getUserEmail(int userId) throws SQLException {
        String query = "SELECT email FROM users WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(1);
                } else {
                    throw new SQLException("User not found.");
                }
            }
        }
    }

    private int getCurrentMonth() {
        return java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1;
    }

    private int getCurrentYear() {
        return java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    }
}
