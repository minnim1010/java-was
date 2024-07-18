package codesquad.application.persistence;

import codesquad.application.model.Article;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository {

    void save(Article article);

    Optional<Article> findById(int articleId);

    List<Article> findAll();

    void deleteAll();
}
