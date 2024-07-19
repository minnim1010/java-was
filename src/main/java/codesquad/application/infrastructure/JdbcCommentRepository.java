package codesquad.application.infrastructure;

import static codesquad.utils.TimeUtils.format;

import codesquad.application.model.Comment;
import codesquad.application.persistence.CommentRepository;
import codesquad.utils.DatabaseConnectionUtils;
import codesquad.utils.TimeUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcCommentRepository implements CommentRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcCommentRepository.class);

    @Override
    public void save(Comment comment) {
        String insertSQL = "INSERT INTO COMMENT (commentId, content, createdAt, userId, articleId) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);) {
            preparedStatement.setString(1, comment.getCommentId());
            preparedStatement.setString(2, comment.getContent());
            preparedStatement.setString(3, format(LocalDateTime.now()));
            preparedStatement.setString(4, comment.getUserId());
            preparedStatement.setString(5, comment.getArticleId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Inserting comment failed");
            }
        } catch (SQLException e) {
            log.error("Failed to insert comment", e);
        }
    }

    @Override
    public Optional<Comment> findById(String commentId) {
        String selectSQL = "SELECT * FROM COMMENT where commentId = ?";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);) {
            preparedStatement.setString(1, commentId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Comment(
                        resultSet.getString("commentId"),
                        resultSet.getString("content"),
                        TimeUtils.toLocalDateTime(resultSet.getString("createdAt")),
                        resultSet.getString("userId"),
                        resultSet.getString("articleId")
                ));
            }
        } catch (SQLException e) {
            log.error("Failed to find comment", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Comment> findAll() {
        String selectSQL = "SELECT * FROM COMMENT";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);) {
            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<Comment> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(new Comment(
                        resultSet.getString("commentId"),
                        resultSet.getString("content"),
                        TimeUtils.toLocalDateTime(resultSet.getString("createdAt")),
                        resultSet.getString("userId"),
                        resultSet.getString("articleId")));
            }
            return result;
        } catch (SQLException e) {
            log.error("Failed to find comment", e);
        }

        return List.of();
    }

    @Override
    public List<Comment> findAllByArticleId(String articleId) {
        String selectSQL = "SELECT * FROM COMMENT WHERE articleId = ?";
        List<Comment> comments = new ArrayList<>();

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);) {
            preparedStatement.setString(1, articleId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                comments.add(new Comment(
                        resultSet.getString("commentId"),
                        resultSet.getString("content"),
                        TimeUtils.toLocalDateTime(resultSet.getString("createdAt")),
                        resultSet.getString("userId"),
                        resultSet.getString("articleId")
                ));
            }
        } catch (SQLException e) {
            log.error("Failed to find comment", e);
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
            log.error("Failed to delete comment", e);
        }
    }
}
