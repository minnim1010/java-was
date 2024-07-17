package codesquad.application.persistence;

import codesquad.application.model.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    void save(Comment comment);

    Optional<Comment> findById(int commentId);

    List<Comment> findAllByArticleId(int articleId);

    void delete(int commentId);

    void deleteAll();
}
