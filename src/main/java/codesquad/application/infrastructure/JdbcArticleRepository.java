package codesquad.application.infrastructure;

import codesquad.application.model.Article;
import codesquad.application.persistence.ArticleRepository;
import codesquad.utils.DatabaseConnectionUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcArticleRepository implements ArticleRepository {

    @Override
    public void save(Article article) {
        String insertSQL = "INSERT INTO ARTICLE (title, content, userId) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);) {
            preparedStatement.setString(1, article.getTitle());
            preparedStatement.setString(2, article.getContent());
            preparedStatement.setString(3, article.getUserId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Inserting article failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Article> findById(int articleId) {
        String selectSQL = "SELECT * FROM ARTICLE where articleId = ?";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);) {
            preparedStatement.setInt(1, articleId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Article(
                        resultSet.getInt("articleId"),
                        resultSet.getString("title"),
                        resultSet.getString("content"),
                        resultSet.getString("userId")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                        resultSet.getInt("articleId"),
                        resultSet.getString("title"),
                        resultSet.getString("content"),
                        resultSet.getString("userId")));
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return List.of();
    }

    @Override
    public void delete(int articleId) {
        String deleteSQL = "DELETE FROM ARTICLE WHERE articleId = ?";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);) {
            preparedStatement.setInt(1, articleId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Deleting article failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {
        String deleteSQL = "DELETE FROM ARTICLE";

        try (Connection connection = DatabaseConnectionUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
