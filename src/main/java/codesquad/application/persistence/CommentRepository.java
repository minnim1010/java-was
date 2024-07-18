package codesquad.application.persistence;

import codesquad.application.model.Comment;
import java.util.List;

public interface CommentRepository extends Repository<Comment, String> {

    List<Comment> findAllByArticleId(String articleId);

}
