package codesquad.application.infrastructure;

import codesquad.application.model.User;
import codesquad.application.persistence.UserRepository;
import codesquad.utils.DatabaseConnectionUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcUserRepository implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcUserRepository.class);

    public void save(User user) {
        String insertSQL = "INSERT INTO MEMBER (userId, password, name, email) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);) {
            preparedStatement.setString(1, user.getUserId());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setString(4, user.getEmail());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Inserting user failed");
            }
        } catch (SQLException e) {
            log.error("Failed to insert user", e);
        }
    }

    public Optional<User> findById(String userId) {
        String selectSQL = "SELECT * FROM MEMBER where userId = ?";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);) {
            preparedStatement.setString(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new User(
                        resultSet.getString("userId"),
                        resultSet.getString("password"),
                        resultSet.getString("name"),
                        resultSet.getString("email")
                ));
            }
        } catch (SQLException e) {
            log.error("Failed to find user", e);
        }

        return Optional.empty();
    }

    public List<User> findAll() {
        String selectSQL = "SELECT * FROM MEMBER";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);) {
            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<User> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(new User(
                        resultSet.getString("userId"),
                        resultSet.getString("password"),
                        resultSet.getString("name"),
                        resultSet.getString("email")
                ));
            }
            return result;
        } catch (SQLException e) {
            log.error("Failed to find user", e);
        }

        return List.of();
    }

    public void deleteAll() {
        String deleteSQL = "DELETE FROM MEMBER";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to delete user", e);
        }
    }
}
