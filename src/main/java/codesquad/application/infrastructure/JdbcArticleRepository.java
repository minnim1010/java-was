package codesquad.application.infrastructure;

import static codesquad.utils.TimeUtils.format;

import codesquad.application.model.Article;
import codesquad.application.persistence.ArticleRepository;
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

public class JdbcArticleRepository implements ArticleRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcArticleRepository.class);

    @Override
    public void save(Article article) {
        String insertSQL = "INSERT INTO ARTICLE (articleId, title, content, imagePath, createdAt, userId) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);) {
            preparedStatement.setString(1, article.getArticleId());
            preparedStatement.setString(2, article.getTitle());
            preparedStatement.setString(3, article.getContent());
            preparedStatement.setString(4, article.getImagePath());
            preparedStatement.setString(5, format(LocalDateTime.now()));
            preparedStatement.setString(6, article.getUserId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Inserting article failed");
            }
        } catch (SQLException e) {
            log.error("Failed to save article", e);
        }
    }

    @Override
    public Optional<Article> findById(String articleId) {
        String selectSQL = "SELECT * FROM ARTICLE where articleId = ?";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);) {
            preparedStatement.setString(1, articleId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Article(
                        resultSet.getString("articleId"),
                        resultSet.getString("title"),
                        resultSet.getString("content"),
                        resultSet.getString("imagePath"),
                        TimeUtils.toLocalDateTime(resultSet.getString("createdAt")),
                        resultSet.getString("userId")));
            }
        } catch (SQLException e) {
            log.error("Failed to find article", e);
        }

        return Optional.empty();
    }

    public List<Article> findAll() {
        String selectSQL = "SELECT * FROM ARTICLE";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);) {
            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<Article> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(new Article(
                        resultSet.getString("articleId"),
                        resultSet.getString("title"),
                        resultSet.getString("content"),
                        resultSet.getString("imagePath"),
                        TimeUtils.toLocalDateTime(resultSet.getString("createdAt")),
                        resultSet.getString("userId")));
            }
            return result;
        } catch (SQLException e) {
            log.error("Failed to find article", e);
        }

        return List.of();
    }

    @Override
    public void deleteAll() {
        String deleteSQL = "DELETE FROM ARTICLE";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to delete article", e);
        }
    }
}
