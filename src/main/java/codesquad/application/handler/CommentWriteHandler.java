package codesquad.application.handler;

import codesquad.application.model.Comment;
import codesquad.application.persistence.CommentRepository;
import codesquad.http.handler.AbstractDynamicRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import codesquad.template.TemplateContext;
import codesquad.template.TemplateEngine;

public class CommentWriteHandler extends AbstractDynamicRequestHandler {

    private final CommentRepository commentRepository;

    public CommentWriteHandler(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void processGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        String userId = httpRequest.getSession().getAttribute("userId");
        String articleId = httpRequest.getQuery("articleId");

        TemplateContext templateContext = new TemplateContext();
        templateContext.setValue("user", userId);
        templateContext.setValue("articleId", articleId);

        String renderedTemplate = TemplateEngine.getInstance().render("/comment_write.html", templateContext);

        httpResponse.setBody(renderedTemplate.getBytes());
        httpResponse.setHeader("Content-Type", "text/html");
        httpResponse.setStatus(HttpStatus.OK);
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
