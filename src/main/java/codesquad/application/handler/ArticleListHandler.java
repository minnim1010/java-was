package codesquad.application.handler;

import static codesquad.http.header.HeaderField.CONTENT_TYPE;

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
        List<Article> articles = articleRepository.findAllByOrderByCreatedAtLimit5();
        Article firstArticle = articles.isEmpty() ? null : articles.get(0);
        List<Comment> comments = commentRepository.findAllByArticleId(
                firstArticle != null ? firstArticle.getArticleId() : 0);

        TemplateContext templateContext = new TemplateContext();
        templateContext.setValue("articles", articles);
        templateContext.setValue("article", articles.get(0));
        templateContext.setValue("comments", comments);
        Session session = httpRequest.getSession();
        if (session != null && session.getAttribute("userId") != null) {
            templateContext.setValue("user", session.getAttribute("userId"));
        }

        byte[] renderedFileContent = TemplateEngine.getInstance().render("/index.html", templateContext).getBytes();

        httpResponse.setBody(renderedFileContent);
        httpResponse.setHeader(CONTENT_TYPE.getFieldName(), "text/html");
        httpResponse.setStatus(HttpStatus.OK);
    }
}
