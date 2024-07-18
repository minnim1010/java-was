package codesquad.application.handler;

import static codesquad.http.header.HeaderField.CONTENT_TYPE;

import codesquad.application.dto.ArticleCommentResponse;
import codesquad.application.dto.CommentResponse;
import codesquad.application.model.Article;
import codesquad.application.model.Comment;
import codesquad.application.persistence.ArticleRepository;
import codesquad.application.persistence.CommentRepository;
import codesquad.http.handler.AbstractDynamicRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import codesquad.http.session.Session;
import codesquad.template.TemplateContext;
import codesquad.template.TemplateEngine;
import java.util.List;

public class ArticleListHandler extends AbstractDynamicRequestHandler {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    public ArticleListHandler(ArticleRepository articleRepository,
                              CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public void processGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        List<Article> articles = articleRepository.findAll();
        List<ArticleCommentResponse> articleCommentResponses = articles.stream()
                .map(this::convertToArticleCommentResponse)
                .toList();

        TemplateContext templateContext = new TemplateContext();
        templateContext.setValue("articles", articleCommentResponses);
        Session session = httpRequest.getSession();
        if (session != null && session.getAttribute("userId") != null) {
            templateContext.setValue("user", session.getAttribute("userId"));
        }

        byte[] renderedFileContent = TemplateEngine.getInstance().render("/index.html", templateContext).getBytes();

        httpResponse.setBody(renderedFileContent);
        httpResponse.setHeader(CONTENT_TYPE.getFieldName(), "text/html");
        httpResponse.setStatus(HttpStatus.OK);
    }

    private ArticleCommentResponse convertToArticleCommentResponse(Article article) {
        List<CommentResponse> commentResponses = commentRepository.findAllByArticleId(article.getArticleId()).stream()
                .map(this::convertToCommentResponse)
                .toList();
        return new ArticleCommentResponse(
                article.getArticleId(),
                article.getTitle(),
                article.getContent(),
                article.getUserId(),
                article.getCreatedAt(),
                article.getImagePath(),
                commentResponses
        );
    }

    private CommentResponse convertToCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getCommentId(),
                comment.getContent(),
                comment.getUserId()
        );
    }
}
