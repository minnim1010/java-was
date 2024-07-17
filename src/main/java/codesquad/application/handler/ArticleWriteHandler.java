package codesquad.application.handler;

import codesquad.application.model.Article;
import codesquad.application.persistence.ArticleRepository;
import codesquad.http.handler.AbstractDynamicRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;

public class ArticleWriteHandler extends AbstractDynamicRequestHandler {

    private final ArticleRepository articleRepository;

    public ArticleWriteHandler(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public void processPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        String title = httpRequest.getQuery("title");
        String query = httpRequest.getQuery("content");

        String userId = httpRequest.getSession().getAttribute("userId");

        Article article = new Article(title, query, userId);
        articleRepository.save(article);

        httpResponse.sendRedirect("/index.html");
    }
}
