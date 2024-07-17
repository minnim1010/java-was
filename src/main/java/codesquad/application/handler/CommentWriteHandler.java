package codesquad.application.handler;

import codesquad.application.model.Comment;
import codesquad.application.persistence.CommentRepository;
import codesquad.http.handler.AbstractDynamicRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;

public class CommentWriteHandler extends AbstractDynamicRequestHandler {

    private final CommentRepository commentRepository;

    public CommentWriteHandler(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void processPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        String content = httpRequest.getQuery("content");
        int articleId = Integer.parseInt(httpRequest.getQuery("articleId"));

        String userId = httpRequest.getSession().getAttribute("userId");

        Comment comment = new Comment(content, userId, articleId);
        commentRepository.save(comment);

        httpResponse.sendRedirect("/index.html");
    }
}
