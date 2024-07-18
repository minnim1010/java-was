package codesquad.application.infrastructure;

import codesquad.application.model.Comment;
import codesquad.application.persistence.CommentRepository;
import codesquad.utils.DatabaseConnectionUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCommentRepository implements CommentRepository {

    @Override
    public void save(Comment comment) {
        String insertSQL = "INSERT INTO COMMENT (content, userId, articleId) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);) {
            preparedStatement.setString(1, comment.getContent());
            preparedStatement.setString(2, comment.getUserId());
            preparedStatement.setInt(3, comment.getArticleId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Inserting comment failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Comment> findById(int commentId) {
        String selectSQL = "SELECT * FROM COMMENT where commentId = ?";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);) {
            preparedStatement.setInt(1, commentId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Comment(
                        resultSet.getInt("commentId"),
                        resultSet.getString("content"),
                        resultSet.getTimestamp("createdAt").toLocalDateTime(),
                        resultSet.getString("userId"),
                        resultSet.getInt("articleId")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Comment> findAllByArticleId(int articleId) {
        String selectSQL = "SELECT * FROM COMMENT WHERE articleId = ?";
        List<Comment> comments = new ArrayList<>();

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);) {
            preparedStatement.setInt(1, articleId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                comments.add(new Comment(
                        resultSet.getInt("commentId"),
                        resultSet.getString("content"),
                        resultSet.getTimestamp("createdAt").toLocalDateTime(),
                        resultSet.getString("userId"),
                        resultSet.getInt("articleId")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comments;
    }

    @Override
    public void deleteAll() {
        String deleteSQL = "DELETE FROM COMMENT";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
