package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SpendingDAO {
        public void addExpense(int userId, double amount, String description) throws SQLException {
            String query = "INSERT INTO expenses (user_id, amount, description) VALUES (?, ?, ?)";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setDouble(2, amount);
                statement.setString(3, description);
                statement.executeUpdate();
            }
        }

        public double getTotalExpenses(int userId, int month, int year) throws SQLException {
            String query = "SELECT SUM(amount) FROM expenses WHERE user_id = ? AND MONTH(date) = ? AND YEAR(date) = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setInt(2, month);
                statement.setInt(3, year);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getDouble(1);
                    }
                }
            }
            return 0;
        }
    }

